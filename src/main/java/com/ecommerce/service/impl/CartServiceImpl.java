package com.ecommerce.service.impl;

import com.ecommerce.dto.CartResponse;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.exception.UnauthorizedException;
import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);

    private final CartRepository cartRepository;
    private final JdbcTemplate jdbc;

    // Maximum quantity per item (business rule)
    private static final int MAX_QUANTITY_PER_ITEM = 10;

    // Constructor Injection
    public CartServiceImpl(CartRepository cartRepository, JdbcTemplate jdbc) {
        this.cartRepository = cartRepository;
        this.jdbc = jdbc;
        log.info("CartService initialized");
    }

    // ════════════════════════════════════════════════════════════════════════
    // GET OR CREATE CART
    // ════════════════════════════════════════════════════════════════════════
    
    @Override
    public Cart getOrCreateCart(Long userId) {
        log.debug("getOrCreateCart called for userId: {}", userId);
        
        // Edge Case 1: Null user ID
        if (userId == null) {
            log.error("User ID is null");
            throw new BadRequestException("User ID is required");
        }
        
        // Edge Case 2: Invalid user ID
        if (userId <= 0) {
            log.error("Invalid user ID: {}", userId);
            throw new BadRequestException("Invalid user ID");
        }

        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    log.info("Creating new cart for user: {}", userId);
                    return cartRepository.create(userId);
                });
    }

    // ════════════════════════════════════════════════════════════════════════
    // GET USER CART
    // ════════════════════════════════════════════════════════════════════════
    
    @Override
    public CartResponse getUserCart(Long userId) {
        log.info("Fetching cart for user: {}", userId);
        
        // Validate user ID
        validateUserId(userId);
        
        Cart cart = getOrCreateCart(userId);
        List<CartItem> items = cartRepository.findItemsByCartId(cart.getId());
        
        // Handle null items list
        if (items == null) {
            items = Collections.emptyList();
        }
        
        log.info("Found {} items in cart for user: {}", items.size(), userId);
        return buildCartResponse(cart, items);
    }

    // ════════════════════════════════════════════════════════════════════════
    // ADD TO CART
    // ════════════════════════════════════════════════════════════════════════
    
    @Override
    @Transactional
    public CartItem addToCart(Long userId, Long productId, Integer quantity) {
        log.info("Adding to cart - User: {}, Product: {}, Quantity: {}", userId, productId, quantity);
        
        // ─────────────────────────────────────────────────────────────────────
        // VALIDATION
        // ─────────────────────────────────────────────────────────────────────
        
        // Edge Case 1: Null user ID
        validateUserId(userId);
        
        // Edge Case 2: Null product ID
        if (productId == null) {
            log.error("Product ID is null");
            throw new BadRequestException("Product ID is required");
        }
        
        // Edge Case 3: Invalid product ID
        if (productId <= 0) {
            log.error("Invalid product ID: {}", productId);
            throw new BadRequestException("Invalid product ID");
        }
        
        // Edge Case 4: Null quantity
        if (quantity == null) {
            log.error("Quantity is null");
            throw new BadRequestException("Quantity is required");
        }
        
        // Edge Case 5: Zero or negative quantity
        if (quantity <= 0) {
            log.error("Invalid quantity: {}", quantity);
            throw new BadRequestException("Quantity must be greater than zero");
        }
        
        // Edge Case 6: Quantity exceeds maximum
        if (quantity > MAX_QUANTITY_PER_ITEM) {
            log.error("Quantity {} exceeds maximum {}", quantity, MAX_QUANTITY_PER_ITEM);
            throw new BadRequestException("Maximum " + MAX_QUANTITY_PER_ITEM + " items allowed per product");
        }

        // ─────────────────────────────────────────────────────────────────────
        // PRODUCT VALIDATION
        // ─────────────────────────────────────────────────────────────────────
        
        // Edge Case 7: Product doesn't exist or is inactive
        BigDecimal productPrice = getProductPrice(productId);
        
        // ─────────────────────────────────────────────────────────────────────
        // CART OPERATIONS
        // ─────────────────────────────────────────────────────────────────────
        
        Cart cart = getOrCreateCart(userId);
        Optional<CartItem> existingItemOpt = cartRepository.findCartItem(cart.getId(), productId);

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + quantity;
            
            // Edge Case 8: Combined quantity exceeds maximum
            if (newQuantity > MAX_QUANTITY_PER_ITEM) {
                log.warn("Combined quantity {} exceeds maximum {}", newQuantity, MAX_QUANTITY_PER_ITEM);
                throw new BadRequestException("Cannot add more. Maximum " + MAX_QUANTITY_PER_ITEM + 
                        " items allowed. You already have " + existingItem.getQuantity() + " in cart.");
            }
            
            cartRepository.updateItemQuantity(existingItem.getId(), newQuantity);
            existingItem.setQuantity(newQuantity);
            
            log.info("Updated existing cart item. New quantity: {}", newQuantity);
            return existingItem;
            
        } else {
            CartItem newItem = new CartItem();
            newItem.setCartId(cart.getId());
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            newItem.setPriceAtAdd(productPrice);
            
            CartItem savedItem = cartRepository.addItem(newItem);
            log.info("Added new item to cart. Item ID: {}", savedItem.getId());
            return savedItem;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // UPDATE CART ITEM QUANTITY
    // ════════════════════════════════════════════════════════════════════════
    
    @Override
    @Transactional
    public CartItem updateCartItemQuantity(Long userId, Long cartItemId, Integer quantity) {
        log.info("Updating cart item - User: {}, ItemId: {}, NewQuantity: {}", userId, cartItemId, quantity);
        
        // ─────────────────────────────────────────────────────────────────────
        // VALIDATION
        // ─────────────────────────────────────────────────────────────────────
        
        validateUserId(userId);
        
        // Edge Case 1: Null cart item ID
        if (cartItemId == null) {
            log.error("Cart item ID is null");
            throw new BadRequestException("Cart item ID is required");
        }
        
        // Edge Case 2: Invalid cart item ID
        if (cartItemId <= 0) {
            log.error("Invalid cart item ID: {}", cartItemId);
            throw new BadRequestException("Invalid cart item ID");
        }
        
        // Edge Case 3: Null quantity
        if (quantity == null) {
            log.error("Quantity is null");
            throw new BadRequestException("Quantity is required");
        }
        
        // Edge Case 4: Zero or negative quantity
        if (quantity <= 0) {
            log.error("Invalid quantity: {}. Use removeFromCart for deletion.", quantity);
            throw new BadRequestException("Quantity must be greater than zero. Use remove endpoint to delete item.");
        }
        
        // Edge Case 5: Quantity exceeds maximum
        if (quantity > MAX_QUANTITY_PER_ITEM) {
            log.error("Quantity {} exceeds maximum {}", quantity, MAX_QUANTITY_PER_ITEM);
            throw new BadRequestException("Maximum " + MAX_QUANTITY_PER_ITEM + " items allowed per product");
        }

        // ─────────────────────────────────────────────────────────────────────
        // AUTHORIZATION CHECK
        // ─────────────────────────────────────────────────────────────────────
        
        // Edge Case 6: Cart item not found
        CartItem item = cartRepository.findItemById(cartItemId)
                .orElseThrow(() -> {
                    log.error("Cart item not found: {}", cartItemId);
                    return new ResourceNotFoundException("Cart item not found with ID: " + cartItemId);
                });

        // Edge Case 7: Cart not found
        Cart cart = cartRepository.findById(item.getCartId())
                .orElseThrow(() -> {
                    log.error("Cart not found for item: {}", cartItemId);
                    return new ResourceNotFoundException("Cart not found");
                });

        // Edge Case 8: Unauthorized access
        if (!cart.getUserId().equals(userId)) {
            log.error("Unauthorized access. User {} trying to modify cart of user {}", userId, cart.getUserId());
            throw new UnauthorizedException("You are not authorized to modify this cart item");
        }

        // ─────────────────────────────────────────────────────────────────────
        // UPDATE
        // ─────────────────────────────────────────────────────────────────────
        
        cartRepository.updateItemQuantity(cartItemId, quantity);
        item.setQuantity(quantity);
        
        log.info("Cart item {} updated to quantity {}", cartItemId, quantity);
        return item;
    }

    // ════════════════════════════════════════════════════════════════════════
    // REMOVE FROM CART
    // ════════════════════════════════════════════════════════════════════════
    
    @Override
    @Transactional
    public void removeFromCart(Long userId, Long cartItemId) {
        log.info("Removing from cart - User: {}, ItemId: {}", userId, cartItemId);
        
        // ─────────────────────────────────────────────────────────────────────
        // VALIDATION
        // ─────────────────────────────────────────────────────────────────────
        
        validateUserId(userId);
        
        // Edge Case 1: Null cart item ID
        if (cartItemId == null) {
            log.error("Cart item ID is null");
            throw new BadRequestException("Cart item ID is required");
        }
        
        // Edge Case 2: Invalid cart item ID
        if (cartItemId <= 0) {
            log.error("Invalid cart item ID: {}", cartItemId);
            throw new BadRequestException("Invalid cart item ID");
        }

        // Edge Case 3: Cart item not found
        CartItem item = cartRepository.findItemById(cartItemId)
                .orElseThrow(() -> {
                    log.error("Cart item not found: {}", cartItemId);
                    return new ResourceNotFoundException("Cart item not found with ID: " + cartItemId);
                });

        // Edge Case 4: Cart not found
        Cart cart = cartRepository.findById(item.getCartId())
                .orElseThrow(() -> {
                    log.error("Cart not found for item: {}", cartItemId);
                    return new ResourceNotFoundException("Cart not found");
                });

        // Edge Case 5: Unauthorized access
        if (!cart.getUserId().equals(userId)) {
            log.error("Unauthorized access. User {} trying to remove from cart of user {}", userId, cart.getUserId());
            throw new UnauthorizedException("You are not authorized to remove this cart item");
        }

        // ─────────────────────────────────────────────────────────────────────
        // DELETE
        // ─────────────────────────────────────────────────────────────────────
        
        cartRepository.removeItem(cartItemId);
        log.info("Cart item {} removed successfully", cartItemId);
    }

    // ════════════════════════════════════════════════════════════════════════
    // CLEAR CART
    // ════════════════════════════════════════════════════════════════════════
    
    @Override
    @Transactional
    public void clearCart(Long userId) {
        log.info("Clearing cart for user: {}", userId);
        
        validateUserId(userId);
        
        Cart cart = getOrCreateCart(userId);
        
        // Get item count before clearing (for logging)
        int itemCount = cartRepository.getItemsCount(cart.getId());
        
        cartRepository.clearCart(cart.getId());
        
        log.info("Cleared {} items from cart for user: {}", itemCount, userId);
    }

    // ════════════════════════════════════════════════════════════════════════
    // VALIDATE CART
    // ════════════════════════════════════════════════════════════════════════
    
    @Override
    public CartResponse validateCart(Long userId) {
        log.info("Validating cart for user: {}", userId);
        
        validateUserId(userId);
        
        CartResponse response = getUserCart(userId);
        
        // Edge Case: Empty cart
        if (response.getItems() == null || response.getItems().isEmpty()) {
            log.warn("Cart is empty for user: {}", userId);
            throw new BadRequestException("Your cart is empty. Add items before checkout.");
        }
        
        // Additional validation: Check if all products are still available
        for (CartItem item : response.getItems()) {
            try {
                getProductPrice(item.getProductId());
            } catch (ResourceNotFoundException e) {
                log.error("Product {} no longer available", item.getProductId());
                throw new BadRequestException("Product with ID '" + item.getProductId() + 
                        "' is no longer available. Please remove it from your cart.");
            }
        }
        
        log.info("Cart validation passed for user: {}", userId);
        return response;
    }

    // ════════════════════════════════════════════════════════════════════════
    // PRIVATE HELPER METHODS
    // ════════════════════════════════════════════════════════════════════════

    private void validateUserId(Long userId) {
        if (userId == null) {
            log.error("User ID is null");
            throw new BadRequestException("User ID is required");
        }
        if (userId <= 0) {
            log.error("Invalid user ID: {}", userId);
            throw new BadRequestException("Invalid user ID");
        }
    }

    /**
     * Get product price from database.
     * Validates that product exists.
     */
    private BigDecimal getProductPrice(Long productId) {
        String sql = "SELECT selling_price FROM products WHERE id = ?";
        try {
            BigDecimal price = jdbc.queryForObject(sql, BigDecimal.class, productId);
            if (price == null) {
                throw new ResourceNotFoundException("Product price not available for ID: " + productId);
            }
            return price;
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            log.error("Product not found with ID: {}", productId);
            throw new ResourceNotFoundException("Product not found with ID: " + productId);
        }
    }

    /**
     * Build CartResponse from Cart and CartItems
     */
    private CartResponse buildCartResponse(Cart cart, List<CartItem> items) {
        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        response.setItems(items);
        
        // Calculate totals
        int totalItems = items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        response.setTotalItems(totalItems);
        
        BigDecimal subtotal = items.stream()
                .map(item -> {
                    BigDecimal price = item.getPriceAtAdd();
                    if (price == null) {
                        price = BigDecimal.ZERO;
                    }
                    return price.multiply(new BigDecimal(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        response.setSubtotal(subtotal);
        response.setTotal(subtotal);
        
        // Group by Shop for Split Orders (handle null shopId)
        Map<Long, List<CartItem>> itemsByShop = items.stream()
                .filter(item -> item.getShopId() != null)
                .collect(Collectors.groupingBy(CartItem::getShopId));
        response.setItemsByShop(itemsByShop);
        
        return response;
    }
}