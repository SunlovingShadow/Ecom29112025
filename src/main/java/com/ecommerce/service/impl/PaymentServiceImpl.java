package com.ecommerce.service.impl;

import com.ecommerce.dto.OrderItemResponse;
import com.ecommerce.dto.PaymentRequest;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.Order;
import com.ecommerce.model.Payment;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.PaymentRepository;
import com.ecommerce.service.InventoryService;
import com.ecommerce.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    
    private static final boolean SIMULATION_ENABLED = false;  // <-- CHANGE TO true FOR SME DEMO
    
    /**
     * SUCCESS RATE (only used when SIMULATION_ENABLED = true)
     * 0.5 = 50% success rate
     */
    private static final double SUCCESS_RATE = 0.5;
    
   
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
   
    private static final DateTimeFormatter LOG_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");


    private final PaymentRepository paymentRepo;
    private final OrderRepository orderRepo;
    private final InventoryService inventoryService;

    public PaymentServiceImpl(PaymentRepository paymentRepo, 
                             OrderRepository orderRepo,
                             InventoryService inventoryService) {
        this.paymentRepo = paymentRepo;
        this.orderRepo = orderRepo;
        this.inventoryService = inventoryService;
        
        log.info("════════════════════════════════════════════════════════════");
        log.info("PaymentService Initialized");
        log.info("Simulation Mode: {}", SIMULATION_ENABLED ? "ENABLED (" + (int)(SUCCESS_RATE * 100) + "% success)" : "DISABLED (100% success)");
        log.info("════════════════════════════════════════════════════════════");
    }
    
    @Override
    @Transactional
    public Payment processPayment(PaymentRequest request) {
        String correlationId = generateCorrelationId();
        LocalDateTime startTime = LocalDateTime.now();
        String finalStatus = "UNKNOWN";
        
        logPaymentStart(correlationId, request, startTime);
        
        try {
            // ─────────────────────────────────────────────────────────────────
            // STEP 1: VALIDATE REQUEST
            // ─────────────────────────────────────────────────────────────────
            log.info("[{}] STEP 1: Validating request...", correlationId);
            validateRequest(request, correlationId);
            log.info("[{}] STEP 1: ✓ Request validation passed", correlationId);

            // ─────────────────────────────────────────────────────────────────
            // STEP 2: VALIDATE ORDER
            // ─────────────────────────────────────────────────────────────────
            log.info("[{}] STEP 2: Validating order...", correlationId);
            Order order = validateAndGetOrder(request.getOrderId(), correlationId);
            log.info("[{}] STEP 2: ✓ Order validation passed (Status: {}, PaymentStatus: {})", 
                    correlationId, order.getStatus(), order.getPaymentStatus());

            // ─────────────────────────────────────────────────────────────────
            // STEP 3: VALIDATE AMOUNT
            // ─────────────────────────────────────────────────────────────────
            log.info("[{}] STEP 3: Validating amount...", correlationId);
            validateAmount(request.getAmount(), order.getTotalAmount(), correlationId);
            log.info("[{}] STEP 3: ✓ Amount validation passed (₹{})", correlationId, order.getTotalAmount());

            // ─────────────────────────────────────────────────────────────────
            // STEP 4: CREATE PAYMENT RECORD
            // ─────────────────────────────────────────────────────────────────
            log.info("[{}] STEP 4: Creating payment record...", correlationId);
            Payment payment = createPaymentRecord(request, correlationId);
            log.info("[{}] STEP 4: ✓ Payment record created (TXN: {})", correlationId, payment.getTxnReference());

            // ─────────────────────────────────────────────────────────────────
            // STEP 5: PROCESS PAYMENT (SIMULATION)
            // ─────────────────────────────────────────────────────────────────
            log.info("[{}] STEP 5: Processing payment...", correlationId);
            boolean success = processPaymentGateway(correlationId);

            if (success) {
                // ─────────────────────────────────────────────────────────────
                // SUCCESS PATH
                // ─────────────────────────────────────────────────────────────
                finalStatus = "SUCCESS";
                payment.setStatus("SUCCESS");
                
                log.info("[{}] ══════════════════════════════════════════════", correlationId);
                log.info("[{}] PAYMENT SUCCESS - Executing success path", correlationId);
                log.info("[{}] ══════════════════════════════════════════════", correlationId);

                // Update order status
                log.info("[{}] STEP 6: Updating order status to PAID/CONFIRMED...", correlationId);
                orderRepo.updatePaymentStatus(order.getId(), "PAID", "CONFIRMED");
                log.info("[{}] STEP 6: ✓ Order status updated", correlationId);

                // Consume inventory (graceful - won't fail payment)
                log.info("[{}] STEP 7: Consuming reserved inventory...", correlationId);
                consumeInventory(order.getId(), correlationId);
                log.info("[{}] STEP 7: ✓ Inventory consumed", correlationId);

            } else {
                // ─────────────────────────────────────────────────────────────
                // FAILURE PATH (Only when SIMULATION_ENABLED = true)
                // ─────────────────────────────────────────────────────────────
                finalStatus = "FAILED";
                payment.setStatus("FAILED");
                
                log.warn("[{}] ══════════════════════════════════════════════", correlationId);
                log.warn("[{}] PAYMENT FAILED - Executing failure path", correlationId);
                log.warn("[{}] ══════════════════════════════════════════════", correlationId);

                // Update payment status only (order stays PLACED for retry)
                log.warn("[{}] STEP 6: Updating payment status to FAILED...", correlationId);
                orderRepo.updatePaymentStatus(order.getId(), "FAILED", "PLACED");
                log.warn("[{}] STEP 6: ✓ Payment status updated (Order remains PLACED for retry)", correlationId);
                
                log.warn("[{}] STEP 7: Skipped - Inventory remains reserved", correlationId);
            }

            // ─────────────────────────────────────────────────────────────────
            // STEP 8: SAVE PAYMENT RECORD
            // ─────────────────────────────────────────────────────────────────
            log.info("[{}] STEP 8: Saving payment record...", correlationId);
            Payment savedPayment = paymentRepo.save(payment);
            log.info("[{}] STEP 8: ✓ Payment saved (ID: {})", correlationId, savedPayment.getId());

            return savedPayment;

        } catch (BadRequestException e) {
            finalStatus = "REJECTED";
            log.error("[{}] ✗ PAYMENT REJECTED: {}", correlationId, e.getMessage());
            throw e;
            
        } catch (ResourceNotFoundException e) {
            finalStatus = "NOT_FOUND";
            log.error("[{}] ✗ RESOURCE NOT FOUND: {}", correlationId, e.getMessage());
            throw e;
            
        } catch (Exception e) {
            finalStatus = "ERROR";
            log.error("[{}] ✗ UNEXPECTED ERROR: {}", correlationId, e.getMessage(), e);
            throw new BadRequestException("Payment processing failed: " + e.getMessage());
            
        } finally {
            logPaymentEnd(correlationId, finalStatus, startTime);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // VALIDATION METHODS - ALL EDGE CASES HANDLED
    // ════════════════════════════════════════════════════════════════════════

    private void validateRequest(PaymentRequest request, String correlationId) {
        // Edge Case 1: Null request
        if (request == null) {
            log.error("[{}] Request is null", correlationId);
            throw new BadRequestException("Payment request cannot be null");
        }
        
        // Edge Case 2: Null order ID
        if (request.getOrderId() == null) {
            log.error("[{}] Order ID is null", correlationId);
            throw new BadRequestException("Order ID is required");
        }
        
        // Edge Case 3: Null amount
        if (request.getAmount() == null) {
            log.error("[{}] Amount is null", correlationId);
            throw new BadRequestException("Payment amount is required");
        }
        
        // Edge Case 4: Zero or negative amount
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("[{}] Amount is not positive: {}", correlationId, request.getAmount());
            throw new BadRequestException("Payment amount must be greater than zero");
        }
        
        // Edge Case 5: Null or empty payment method
        if (request.getMethod() == null || request.getMethod().trim().isEmpty()) {
            log.error("[{}] Payment method is empty", correlationId);
            throw new BadRequestException("Payment method is required");
        }
        
        // Edge Case 6: Invalid payment method
        String method = request.getMethod().toUpperCase();
        if (!isValidPaymentMethod(method)) {
            log.error("[{}] Invalid payment method: {}", correlationId, method);
            throw new BadRequestException("Invalid payment method: " + method + ". Supported: UPI, CARD, NET_BANKING, WALLET, COD");
        }
    }

    private boolean isValidPaymentMethod(String method) {
        return method.equals("UPI") || 
               method.equals("CARD") || 
               method.equals("NET_BANKING") || 
               method.equals("WALLET") || 
               method.equals("COD");
    }

    private Order validateAndGetOrder(Long orderId, String correlationId) {
        // Edge Case 7: Order not found
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> {
                    log.error("[{}] Order not found: {}", correlationId, orderId);
                    return new ResourceNotFoundException("Order not found with ID: " + orderId);
                });

        // Edge Case 8: Already paid
        if ("PAID".equalsIgnoreCase(order.getPaymentStatus())) {
            log.error("[{}] Order {} is already paid", correlationId, orderId);
            throw new BadRequestException("Order is already paid. Payment cannot be processed again.");
        }

        // Edge Case 9: Order cancelled
        if ("CANCELLED".equalsIgnoreCase(order.getStatus())) {
            log.error("[{}] Order {} is cancelled", correlationId, orderId);
            throw new BadRequestException("Cannot pay for a cancelled order. Please create a new order.");
        }

        // Edge Case 10: Order already shipped
        if ("SHIPPED".equalsIgnoreCase(order.getStatus())) {
            log.error("[{}] Order {} is already shipped", correlationId, orderId);
            throw new BadRequestException("Order is already shipped. Payment should have been completed before shipping.");
        }

        // Edge Case 11: Order already delivered
        if ("DELIVERED".equalsIgnoreCase(order.getStatus())) {
            log.error("[{}] Order {} is already delivered", correlationId, orderId);
            throw new BadRequestException("Order is already delivered. Payment should have been completed before delivery.");
        }

        // Edge Case 12: Order returned
        if ("RETURNED".equalsIgnoreCase(order.getStatus())) {
            log.error("[{}] Order {} is returned", correlationId, orderId);
            throw new BadRequestException("Cannot pay for a returned order.");
        }

        // Edge Case 13: Unusual state - CONFIRMED but not PAID
        if ("CONFIRMED".equalsIgnoreCase(order.getStatus()) && !"PAID".equalsIgnoreCase(order.getPaymentStatus())) {
            log.warn("[{}] Order {} is CONFIRMED but not PAID - unusual state, allowing payment", correlationId, orderId);
        }

        return order;
    }

    private void validateAmount(BigDecimal requestAmount, BigDecimal orderAmount, String correlationId) {
        // Edge Case 14: Amount mismatch
        if (requestAmount.compareTo(orderAmount) != 0) {
            log.error("[{}] Amount mismatch - Expected: ₹{}, Got: ₹{}", correlationId, orderAmount, requestAmount);
            throw new BadRequestException("Payment amount mismatch! Expected: ₹" + orderAmount + ", Got: ₹" + requestAmount);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // PAYMENT GATEWAY SIMULATION
    // ════════════════════════════════════════════════════════════════════════

    private boolean processPaymentGateway(String correlationId) {
        if (!SIMULATION_ENABLED) {
            // Simulation disabled - always succeed (FOR TESTING)
            log.info("[{}] Payment Gateway: SIMULATION DISABLED - Returning SUCCESS", correlationId);
            return true;
        }
        
        // Simulation enabled - random success/failure (FOR SME DEMO)
        double randomValue = SECURE_RANDOM.nextDouble();
        boolean success = randomValue < SUCCESS_RATE;
        
        log.info("[{}] ┌───────────────────────────────────────┐", correlationId);
        log.info("[{}] │ PAYMENT GATEWAY SIMULATION            │", correlationId);
        log.info("[{}] ├───────────────────────────────────────┤", correlationId);
        log.info("[{}] │ Random Value : {}                │", correlationId, String.format("%.4f", randomValue));
        log.info("[{}] │ Success Rate : {}                  │", correlationId, String.format("%.2f", SUCCESS_RATE));
        log.info("[{}] │ Result       : {}              │", correlationId, success ? "SUCCESS ✓" : "FAILED ✗ ");
        log.info("[{}] └───────────────────────────────────────┘", correlationId);
        
        return success;
    }

    private void consumeInventory(Long orderId, String correlationId) {
        List<OrderItemResponse> items = orderRepo.findItemsByOrderId(orderId);
        
        // Edge Case 15: No items in order
        if (items == null || items.isEmpty()) {
            log.warn("[{}] No items found for order {} - skipping inventory consumption", correlationId, orderId);
            return;
        }
        
        log.info("[{}] Processing {} items for inventory consumption", correlationId, items.size());
        
        int successCount = 0;
        int failCount = 0;
        
        for (OrderItemResponse item : items) {
            try {
                inventoryService.consumeReservedOnOrder(item.getProductId(), item.getQuantity());
                successCount++;
                log.info("[{}]   ✓ Product {}: consumed {} units", 
                        correlationId, item.getProductId(), item.getQuantity());
            } catch (ResourceNotFoundException e) {
                // Edge Case 16: No inventory record - log but don't fail payment
                failCount++;
                log.warn("[{}]   ⚠ Product {}: no inventory record - skipped", 
                        correlationId, item.getProductId());
            } catch (Exception e) {
                // Edge Case 16: Other inventory errors - log but don't fail payment
                failCount++;
                log.warn("[{}]   ⚠ Product {}: consumption failed - {}", 
                        correlationId, item.getProductId(), e.getMessage());
            }
        }
        
        log.info("[{}] Inventory summary: {} succeeded, {} warnings", correlationId, successCount, failCount);
        
        if (failCount > 0) {
            log.warn("[{}] ⚠️ Some inventory consumption failed - manual review may be needed", correlationId);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ════════════════════════════════════════════════════════════════════════

    private Payment createPaymentRecord(PaymentRequest request, String correlationId) {
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setMethod(request.getMethod().toUpperCase());
        payment.setTxnReference(request.getTxnReference() != null ? 
                request.getTxnReference() : generateTxnReference());
        payment.setCreatedAt(LocalDateTime.now());
        return payment;
    }

    private String generateCorrelationId() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateTxnReference() {
        return "TXN-" + System.currentTimeMillis();
    }

    // ════════════════════════════════════════════════════════════════════════
    // LOGGING HELPERS
    // ════════════════════════════════════════════════════════════════════════

    private void logPaymentStart(String correlationId, PaymentRequest request, LocalDateTime startTime) {
        log.info("┌────────────────────────────────────────────────────────────┐");
        log.info("│ PAYMENT PROCESSING STARTED                                 │");
        log.info("├────────────────────────────────────────────────────────────┤");
        log.info("│ Correlation ID : {}                           │", correlationId);
        log.info("│ Timestamp      : {} │", startTime.format(LOG_FORMAT));
        log.info("│ Order ID       : {}                                       │", padRight(String.valueOf(request.getOrderId()), 7));
        log.info("│ Amount         : ₹{}                                   │", padRight(String.valueOf(request.getAmount()), 10));
        log.info("│ Method         : {}                                    │", padRight(request.getMethod(), 9));
        log.info("│ Simulation     : {}                                 │", padRight(SIMULATION_ENABLED ? "ENABLED" : "DISABLED", 12));
        log.info("└────────────────────────────────────────────────────────────┘");
    }

    private void logPaymentEnd(String correlationId, String finalStatus, LocalDateTime startTime) {
        LocalDateTime endTime = LocalDateTime.now();
        long durationMs = java.time.Duration.between(startTime, endTime).toMillis();
        
        log.info("┌────────────────────────────────────────────────────────────┐");
        log.info("│ PAYMENT PROCESSING COMPLETED                               │");
        log.info("├────────────────────────────────────────────────────────────┤");
        log.info("│ Correlation ID : {}                           │", correlationId);
        log.info("│ Final Status   : {}                                  │", padRight(finalStatus, 10));
        log.info("│ Duration       : {} ms                                    │", padRight(String.valueOf(durationMs), 5));
        log.info("│ End Time       : {} │", endTime.format(LOG_FORMAT));
        log.info("└────────────────────────────────────────────────────────────┘");
    }

    private String padRight(String s, int n) {
        if (s == null) s = "NULL";
        return String.format("%-" + n + "s", s);
    }
}