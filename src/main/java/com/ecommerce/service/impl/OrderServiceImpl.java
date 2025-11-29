package com.ecommerce.service.impl;

import com.ecommerce.dto.CartResponse;
import com.ecommerce.dto.InventoryResponse;
import com.ecommerce.dto.OrderItemResponse;
import com.ecommerce.dto.OrderRequest;
import com.ecommerce.dto.OrderResponse;
import com.ecommerce.enums.DiscountType;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.exception.UnauthorizedException;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Coupon;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.repository.CouponRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.service.CartService;
import com.ecommerce.service.InventoryService;
import com.ecommerce.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    
  
    
    private static final int MIN_ADDRESS_LENGTH = 10;
    private static final int MAX_ADDRESS_LENGTH = 500;
    private static final List<String> VALID_ORDER_STATUSES = List.of(
        "PLACED", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED", "RETURNED"
    );

    
    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final CouponRepository couponRepository;
    private final InventoryService inventoryService;

    public OrderServiceImpl(CartService cartService, 
                           OrderRepository orderRepository,
                           CouponRepository couponRepository,
                           InventoryService inventoryService) {
        this.cartService = cartService;
        this.orderRepository = orderRepository;
        this.couponRepository = couponRepository;
        this.inventoryService = inventoryService;
        
        log.info("════════════════════════════════════════════════════════════");
        log.info("OrderService Initialized WITH Inventory Management");
        log.info("════════════════════════════════════════════════════════════");
    }

    // ════════════════════════════════════════════════════════════════════════
    // 1. PLACE ORDER (CHECKOUT) - WITH INVENTORY CHECK & RESERVATION
    // ════════════════════════════════════════════════════════════════════════
    
    @Override
    @Transactional
    public List<Order> placeOrder(Long userId, OrderRequest request) {
        log.info("═══════════════════════════════════════════════════════════");
        log.info("PLACING ORDER - User: {}", userId);
        log.info("═══════════════════════════════════════════════════════════");

        // ─────────────────────────────────────────────────────────────────────
        // STEP 1: VALIDATE REQUEST
        // ─────────────────────────────────────────────────────────────────────
        log.info("STEP 1: Validating request...");
        
        validateUserId(userId);
        validateOrderRequest(request);
        validateShippingAddress(request.getShippingAddress());
        
        log.info("STEP 1: ✓ Request validation passed");

        // ─────────────────────────────────────────────────────────────────────
        // STEP 2: FETCH & VALIDATE CART
        // ─────────────────────────────────────────────────────────────────────
        log.info("STEP 2: Fetching cart...");
        
        CartResponse cartResponse = cartService.getUserCart(userId);
        Map<Long, List<CartItem>> itemsByShop = cartResponse.getItemsByShop();

        if (itemsByShop == null || itemsByShop.isEmpty()) {
            log.error("Cart is empty for user: {}", userId);
            throw new BadRequestException("Cannot place order: Your cart is empty");
        }
        
        log.info("STEP 2: ✓ Cart has {} shop(s)", itemsByShop.size());

        // ─────────────────────────────────────────────────────────────────────
        // STEP 3: CHECK INVENTORY AVAILABILITY (CRITICAL!)
        // ─────────────────────────────────────────────────────────────────────
        log.info("STEP 3: Checking inventory availability...");
        
        List<CartItem> allItems = new ArrayList<>();
        for (List<CartItem> shopItems : itemsByShop.values()) {
            allItems.addAll(shopItems);
        }
        
        checkInventoryAvailability(allItems);
        
        log.info("STEP 3: ✓ All items are in stock");

        // ─────────────────────────────────────────────────────────────────────
        // STEP 4: RESERVE INVENTORY
        // ─────────────────────────────────────────────────────────────────────
        log.info("STEP 4: Reserving inventory...");
        
        List<CartItem> reservedItems = new ArrayList<>();
        try {
            for (CartItem item : allItems) {
                inventoryService.reserveStock(item.getProductId(), item.getQuantity());
                reservedItems.add(item);
                log.info("  ✓ Reserved {} units of product {}", item.getQuantity(), item.getProductId());
            }
            log.info("STEP 4: ✓ All inventory reserved");
        } catch (Exception e) {
            // Rollback any reservations made
            log.error("Failed to reserve inventory: {}", e.getMessage());
            rollbackReservations(reservedItems);
            throw new BadRequestException("Failed to reserve inventory: " + e.getMessage());
        }

        // ─────────────────────────────────────────────────────────────────────
        // STEP 5: VALIDATE COUPON (if provided)
        // ─────────────────────────────────────────────────────────────────────
        log.info("STEP 5: Validating coupon...");
        
        Coupon coupon = null;
        if (request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty()) {
            coupon = validateAndGetCoupon(request.getCouponCode(), userId);
            log.info("STEP 5: ✓ Coupon {} validated", coupon.getCode());
        } else {
            log.info("STEP 5: ✓ No coupon applied");
        }

        // ─────────────────────────────────────────────────────────────────────
        // STEP 6: CREATE ORDERS (Split by Shop)
        // ─────────────────────────────────────────────────────────────────────
        log.info("STEP 6: Creating orders...");
        
        List<Order> createdOrders = new ArrayList<>();
        boolean globalCouponUsed = false;

        try {
            for (Map.Entry<Long, List<CartItem>> entry : itemsByShop.entrySet()) {
                Long shopId = entry.getKey();
                List<CartItem> shopItems = entry.getValue();

                // Calculate shop total
                BigDecimal shopTotal = calculateShopTotal(shopItems);

                // Apply coupon logic
                BigDecimal finalAmount = shopTotal;
                boolean couponAppliedToThisOrder = false;

                if (coupon != null) {
                    CouponApplicationResult result = applyCouponIfApplicable(
                        coupon, shopId, shopTotal, globalCouponUsed
                    );
                    finalAmount = result.finalAmount;
                    couponAppliedToThisOrder = result.applied;
                    if (result.applied && coupon.getShopId() == null) {
                        globalCouponUsed = true;
                    }
                }

                // Create Order
                Order order = new Order();
                order.setUserId(userId);
                order.setShopId(shopId);
                order.setShippingAddress(request.getShippingAddress().trim());
                order.setTotalAmount(finalAmount.setScale(2, RoundingMode.HALF_UP));
                order.setOrderNumber(generateOrderNumber(shopId));
                order.setStatus("PLACED");
                order.setPaymentStatus("PENDING");

                // Save Order
                Order savedOrder = orderRepository.save(order);
                log.info("  ✓ Order {} created for shop {}", savedOrder.getOrderNumber(), shopId);

                // Create Order Items
                List<OrderItem> orderItems = createOrderItems(savedOrder.getId(), shopItems);
                orderRepository.saveOrderItems(orderItems);

                // Record Coupon Usage
                if (couponAppliedToThisOrder && coupon != null) {
                    couponRepository.recordUsage(userId, coupon.getId(), savedOrder.getId());
                    log.info("  ✓ Coupon {} applied to order {}", coupon.getCode(), savedOrder.getId());
                }

                createdOrders.add(savedOrder);
            }
        } catch (Exception e) {
            // If order creation fails, release all reserved inventory
            log.error("Order creation failed: {}", e.getMessage());
            rollbackReservations(allItems);
            throw e;
        }

        // ─────────────────────────────────────────────────────────────────────
        // STEP 7: CLEAR CART
        // ─────────────────────────────────────────────────────────────────────
        log.info("STEP 7: Clearing cart...");
        cartService.clearCart(userId);
        log.info("STEP 7: ✓ Cart cleared");

        log.info("═══════════════════════════════════════════════════════════");
        log.info("ORDER PLACED SUCCESSFULLY - {} order(s) created", createdOrders.size());
        log.info("═══════════════════════════════════════════════════════════");

        return createdOrders;
    }

    // ════════════════════════════════════════════════════════════════════════
    // INVENTORY CHECK - THE KEY METHOD
    // ════════════════════════════════════════════════════════════════════════
    
    /**
     * Check if all items in cart have sufficient inventory
     * Throws BadRequestException with clear message if any item is out of stock
     */
    private void checkInventoryAvailability(List<CartItem> items) {
        List<String> outOfStockItems = new ArrayList<>();
        List<String> insufficientStockItems = new ArrayList<>();
        
        for (CartItem item : items) {
            try {
                InventoryResponse inventory = inventoryService.getInventory(item.getProductId());
                int available = inventory.getAvailable();
                
                if (available <= 0) {
                    // Completely out of stock
                    outOfStockItems.add("Product ID " + item.getProductId() + " is out of stock");
                    log.warn("Product {} is OUT OF STOCK", item.getProductId());
                } else if (available < item.getQuantity()) {
                    // Not enough stock
                    insufficientStockItems.add(
                        "Product ID " + item.getProductId() + 
                        ": requested " + item.getQuantity() + 
                        ", only " + available + " available"
                    );
                    log.warn("Product {} has insufficient stock: requested {}, available {}", 
                            item.getProductId(), item.getQuantity(), available);
                } else {
                    log.debug("Product {} has sufficient stock: {} available, {} requested", 
                            item.getProductId(), available, item.getQuantity());
                }
                
            } catch (ResourceNotFoundException e) {
                // No inventory record at all
                outOfStockItems.add("Product ID " + item.getProductId() + " is not available");
                log.warn("No inventory record for product {}", item.getProductId());
            }
        }
        
        // Build error message if any issues found
        if (!outOfStockItems.isEmpty() || !insufficientStockItems.isEmpty()) {
            StringBuilder errorMsg = new StringBuilder("Cannot place order:\n");
            
            if (!outOfStockItems.isEmpty()) {
                errorMsg.append("\n❌ OUT OF STOCK:\n");
                for (String msg : outOfStockItems) {
                    errorMsg.append("  • ").append(msg).append("\n");
                }
            }
            
            if (!insufficientStockItems.isEmpty()) {
                errorMsg.append("\n⚠️ INSUFFICIENT STOCK:\n");
                for (String msg : insufficientStockItems) {
                    errorMsg.append("  • ").append(msg).append("\n");
                }
            }
            
            errorMsg.append("\nPlease update your cart and try again.");
            
            log.error("Inventory check failed: {} out of stock, {} insufficient", 
                     outOfStockItems.size(), insufficientStockItems.size());
            throw new BadRequestException(errorMsg.toString());
        }
    }
    
    /**
     * Rollback reservations if order creation fails
     */
    private void rollbackReservations(List<CartItem> reservedItems) {
        log.warn("Rolling back {} reservations...", reservedItems.size());
        
        for (CartItem item : reservedItems) {
            try {
                inventoryService.releaseReserved(item.getProductId(), item.getQuantity());
                log.info("  ✓ Released {} units of product {}", item.getQuantity(), item.getProductId());
            } catch (Exception e) {
                log.error("  ✗ Failed to release product {}: {}", item.getProductId(), e.getMessage());
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // 2. GET USER ORDERS
    // ════════════════════════════════════════════════════════════════════════
    
    @Override
    public List<OrderResponse> getUserOrders(Long userId) {
        log.info("Fetching orders for user: {}", userId);
        
        validateUserId(userId);
        
        List<Order> orders = orderRepository.findByUserId(userId);
        List<OrderResponse> responseList = new ArrayList<>();
        
        for (Order order : orders) {
            responseList.add(mapToResponse(order, null));
        }
        
        log.info("Found {} orders for user: {}", responseList.size(), userId);
        return responseList;
    }

    // ════════════════════════════════════════════════════════════════════════
    // 3. GET ORDER DETAILS
    // ════════════════════════════════════════════════════════════════════════
    
    @Override
    public OrderResponse getOrderDetails(Long orderId) {
        log.info("Fetching order details for order: {}", orderId);
        
        validateOrderId(orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found: {}", orderId);
                    return new ResourceNotFoundException("Order not found with ID: " + orderId);
                });
        
        List<OrderItemResponse> items = orderRepository.findItemsByOrderId(orderId);
        return mapToResponse(order, items);
    }

    // ════════════════════════════════════════════════════════════════════════
    // 4. CANCEL ORDER - WITH INVENTORY RELEASE
    // ════════════════════════════════════════════════════════════════════════
    
    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        log.info("Cancelling order: {}", orderId);
        
        validateOrderId(orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found: {}", orderId);
                    return new ResourceNotFoundException("Order not found with ID: " + orderId);
                });

        String status = order.getStatus().toUpperCase();
        
        // Validate cancellation is allowed
        validateCancellationAllowed(status, orderId);

        // Release reserved inventory
        releaseOrderInventory(orderId);

        // Update order status
        orderRepository.updateOrderStatus(orderId, "CANCELLED");
        log.info("Order {} cancelled successfully", orderId);
    }
    
    /**
     * Cancel order with user authorization check
     */
    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        log.info("User {} attempting to cancel order: {}", userId, orderId);
        
        validateUserId(userId);
        validateOrderId(orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        
        // Check authorization
        if (!order.getUserId().equals(userId)) {
            log.error("User {} not authorized to cancel order {} (belongs to user {})", 
                     userId, orderId, order.getUserId());
            throw new UnauthorizedException("You are not authorized to cancel this order");
        }
        
        cancelOrder(orderId);
    }
    
    /**
     * Release inventory when order is cancelled
     */
    private void releaseOrderInventory(Long orderId) {
        log.info("Releasing inventory for cancelled order: {}", orderId);
        
        List<OrderItemResponse> items = orderRepository.findItemsByOrderId(orderId);
        
        if (items == null || items.isEmpty()) {
            log.warn("No items found for order {} - skipping inventory release", orderId);
            return;
        }
        
        for (OrderItemResponse item : items) {
            try {
                inventoryService.releaseReserved(item.getProductId(), item.getQuantity());
                log.info("  ✓ Released {} units of product {}", item.getQuantity(), item.getProductId());
            } catch (Exception e) {
                log.warn("  ⚠ Could not release product {}: {}", item.getProductId(), e.getMessage());
            }
        }
    }
    
    private void validateCancellationAllowed(String status, Long orderId) {
        if ("SHIPPED".equals(status)) {
            throw new BadRequestException(
                "Cannot cancel order: It has already been shipped. Please wait for delivery and request a return instead."
            );
        }
        if ("DELIVERED".equals(status)) {
            throw new BadRequestException(
                "Cannot cancel order: It has already been delivered. Please request a return instead."
            );
        }
        if ("CANCELLED".equals(status)) {
            throw new BadRequestException("Order has already been cancelled");
        }
        if ("RETURNED".equals(status)) {
            throw new BadRequestException("Cannot cancel order: It has already been returned");
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // 5. ADMIN: GET ALL ORDERS
    // ════════════════════════════════════════════════════════════════════════
    
    @Override
    public List<OrderResponse> getAllOrders() {
        log.info("Admin fetching all orders");
        
        List<Order> orders = orderRepository.findAll();
        List<OrderResponse> responseList = new ArrayList<>();
        
        for (Order order : orders) {
            responseList.add(mapToResponse(order, null));
        }
        
        log.info("Found {} total orders", responseList.size());
        return responseList;
    }

    // ════════════════════════════════════════════════════════════════════════
    // 6. ADMIN: UPDATE ORDER STATUS
    // ════════════════════════════════════════════════════════════════════════
    
    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String newStatus) {
        log.info("Updating order {} status to: {}", orderId, newStatus);
        
        validateOrderId(orderId);
        validateStatus(newStatus);
        
        String normalizedStatus = newStatus.trim().toUpperCase();
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        String currentStatus = order.getStatus().toUpperCase();
        
        if ("DELIVERED".equals(currentStatus)) {
            throw new BadRequestException("Cannot change status of a delivered order.");
        }
        if ("CANCELLED".equals(currentStatus)) {
            throw new BadRequestException("Cannot change status of a cancelled order.");
        }

        orderRepository.updateOrderStatus(orderId, normalizedStatus);
        order.setStatus(normalizedStatus);
        
        log.info("Order {} status updated from {} to {}", orderId, currentStatus, normalizedStatus);
        return mapToResponse(order, null);
    }

    // ════════════════════════════════════════════════════════════════════════
    // VALIDATION HELPERS
    // ════════════════════════════════════════════════════════════════════════
    
    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new BadRequestException("User ID is required");
        }
        if (userId <= 0) {
            throw new BadRequestException("Invalid user ID: " + userId);
        }
    }
    
    private void validateOrderId(Long orderId) {
        if (orderId == null) {
            throw new BadRequestException("Order ID is required");
        }
        if (orderId <= 0) {
            throw new BadRequestException("Invalid order ID: " + orderId);
        }
    }
    
    private void validateOrderRequest(OrderRequest request) {
        if (request == null) {
            throw new BadRequestException("Order request is required");
        }
    }
    
    private void validateStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new BadRequestException("Order status is required");
        }
        String normalized = status.trim().toUpperCase();
        if (!VALID_ORDER_STATUSES.contains(normalized)) {
            throw new BadRequestException(
                "Invalid order status: " + status + 
                ". Valid statuses are: " + String.join(", ", VALID_ORDER_STATUSES)
            );
        }
    }
    
    private void validateShippingAddress(String address) {
        if (address == null) {
            throw new BadRequestException("Shipping address is required");
        }
        
        String trimmedAddress = address.trim();
        
        if (trimmedAddress.isEmpty()) {
            throw new BadRequestException("Shipping address is required");
        }
        
        if (trimmedAddress.length() < MIN_ADDRESS_LENGTH) {
            throw new BadRequestException(
                "Shipping address is too short. Please provide a complete address (minimum " + 
                MIN_ADDRESS_LENGTH + " characters)"
            );
        }
        
        if (trimmedAddress.length() > MAX_ADDRESS_LENGTH) {
            throw new BadRequestException(
                "Shipping address is too long. Maximum " + MAX_ADDRESS_LENGTH + " characters allowed"
            );
        }
        
        if (!trimmedAddress.matches(".*[a-zA-Z].*")) {
            throw new BadRequestException(
                "Invalid shipping address. Address must contain letters, not just numbers"
            );
        }
    }
    
    private Coupon validateAndGetCoupon(String couponCode, Long userId) {
        String normalizedCode = couponCode.trim().toUpperCase();
        
        Coupon coupon = couponRepository.findByCode(normalizedCode)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Coupon Code: " + normalizedCode));
        
        if (coupon.getValidFrom() != null && coupon.getValidFrom().isAfter(LocalDate.now())) {
            throw new BadRequestException("Coupon is not yet valid. It becomes active on: " + coupon.getValidFrom());
        }
        
        if (coupon.getValidTo() != null && coupon.getValidTo().isBefore(LocalDate.now())) {
            throw new BadRequestException("Coupon has expired on: " + coupon.getValidTo());
        }
        
        if (couponRepository.isUsedByUser(userId, coupon.getId())) {
            throw new BadRequestException("You have already used this coupon");
        }
        
        return coupon;
    }

    // ════════════════════════════════════════════════════════════════════════
    // CALCULATION HELPERS
    // ════════════════════════════════════════════════════════════════════════
    
    private BigDecimal calculateShopTotal(List<CartItem> shopItems) {
        return shopItems.stream()
                .map(item -> item.getPriceAtAdd().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private CouponApplicationResult applyCouponIfApplicable(
            Coupon coupon, Long shopId, BigDecimal shopTotal, boolean globalCouponUsed) {
        
        CouponApplicationResult result = new CouponApplicationResult();
        result.finalAmount = shopTotal;
        result.applied = false;
        
        // Shop-Specific Coupon
        if (coupon.getShopId() != null && coupon.getShopId().equals(shopId)) {
            if (shopTotal.compareTo(coupon.getMinOrderAmount()) >= 0) {
                result.finalAmount = applyDiscount(shopTotal, coupon);
                result.applied = true;
            }
        }
        // Global Coupon (Apply once)
        else if (coupon.getShopId() == null && !globalCouponUsed) {
            if (shopTotal.compareTo(coupon.getMinOrderAmount()) >= 0) {
                result.finalAmount = applyDiscount(shopTotal, coupon);
                result.applied = true;
            }
        }
        
        return result;
    }
    
    private static class CouponApplicationResult {
        BigDecimal finalAmount;
        boolean applied;
    }
    
    private BigDecimal applyDiscount(BigDecimal total, Coupon coupon) {
        BigDecimal result;
        
        if (DiscountType.FLAT == coupon.getDiscountType()) {
            result = total.subtract(coupon.getDiscountValue());
        } else {
            BigDecimal discountAmount = total
                    .multiply(coupon.getDiscountValue())
                    .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            result = total.subtract(discountAmount);
        }
        
        return result.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : result;
    }
    
    private String generateOrderNumber(Long shopId) {
        return "ORD-" + System.currentTimeMillis() + "-" + shopId;
    }
    
    private List<OrderItem> createOrderItems(Long orderId, List<CartItem> shopItems) {
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (CartItem ci : shopItems) {
            BigDecimal lineTotal = ci.getPriceAtAdd().multiply(new BigDecimal(ci.getQuantity()));
            orderItems.add(new OrderItem(orderId, ci.getProductId(), ci.getQuantity(), ci.getPriceAtAdd(), lineTotal));
        }
        
        return orderItems;
    }
    
    private OrderResponse mapToResponse(Order order, List<OrderItemResponse> items) {
        OrderResponse dto = new OrderResponse();
        dto.setOrderId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setCreatedAt(order.getCreatedAt() != null ? order.getCreatedAt().toString() : null);
        dto.setItems(items);
        return dto;
    }
}