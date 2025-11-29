package com.ecommerce.service.impl;

import com.ecommerce.dto.ReturnRequestDTO;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.exception.UnauthorizedException;
import com.ecommerce.model.Order;
import com.ecommerce.model.ReturnRequest;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ReturnRepository;
import com.ecommerce.service.ReturnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class ReturnServiceImpl implements ReturnService {

    private static final Logger log = LoggerFactory.getLogger(ReturnServiceImpl.class);

    private final ReturnRepository returnRepository;
    private final OrderRepository orderRepository;

    // Business rule: Return window in days
    private static final int RETURN_WINDOW_DAYS = 7;

    // Constructor Injection
    public ReturnServiceImpl(ReturnRepository returnRepository, OrderRepository orderRepository) {
        this.returnRepository = returnRepository;
        this.orderRepository = orderRepository;
        log.info("ReturnService initialized with {} day return window", RETURN_WINDOW_DAYS);
    }

    // ════════════════════════════════════════════════════════════════════════
    // REQUEST RETURN
    // ════════════════════════════════════════════════════════════════════════
    
    @Override
    @Transactional
    public void requestReturn(Long userId, ReturnRequestDTO requestDto) {
        log.info("Processing return request - User: {}, Order: {}", userId, 
                requestDto != null ? requestDto.getOrderId() : "NULL");
        
        // ─────────────────────────────────────────────────────────────────────
        // INPUT VALIDATION
        // ─────────────────────────────────────────────────────────────────────
        
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
        
        // Edge Case 3: Null request DTO
        if (requestDto == null) {
            log.error("Return request DTO is null");
            throw new BadRequestException("Return request data is required");
        }
        
        // Edge Case 4: Null order ID
        if (requestDto.getOrderId() == null) {
            log.error("Order ID is null");
            throw new BadRequestException("Order ID is required");
        }
        
        // Edge Case 5: Invalid order ID
        if (requestDto.getOrderId() <= 0) {
            log.error("Invalid order ID: {}", requestDto.getOrderId());
            throw new BadRequestException("Invalid order ID");
        }
        
        // Edge Case 6: Null or empty reason
        if (requestDto.getReason() == null || requestDto.getReason().trim().isEmpty()) {
            log.error("Return reason is empty");
            throw new BadRequestException("Return reason is required");
        }
        
        // Edge Case 7: Reason too short
        if (requestDto.getReason().trim().length() < 10) {
            log.error("Return reason too short: {}", requestDto.getReason().length());
            throw new BadRequestException("Please provide a more detailed reason (minimum 10 characters)");
        }
        
        // Edge Case 8: Reason too long
        if (requestDto.getReason().length() > 500) {
            log.error("Return reason too long: {}", requestDto.getReason().length());
            throw new BadRequestException("Return reason is too long (maximum 500 characters)");
        }

        // ─────────────────────────────────────────────────────────────────────
        // ORDER VALIDATION
        // ─────────────────────────────────────────────────────────────────────
        
        // Edge Case 9: Order not found
        Order order = orderRepository.findById(requestDto.getOrderId())
                .orElseThrow(() -> {
                    log.error("Order not found: {}", requestDto.getOrderId());
                    return new ResourceNotFoundException("Order not found with ID: " + requestDto.getOrderId());
                });

        // Edge Case 10: Order doesn't belong to user
        if (!order.getUserId().equals(userId)) {
            log.error("Unauthorized return request. User {} trying to return order of user {}", 
                    userId, order.getUserId());
            throw new UnauthorizedException("You are not authorized to return this order");
        }

        // Edge Case 11: Order status is PLACED (not yet shipped)
        if ("PLACED".equalsIgnoreCase(order.getStatus())) {
            log.error("Cannot return order {} - status is PLACED", order.getId());
            throw new BadRequestException("This order has not been shipped yet. Please cancel instead of return.");
        }

        // Edge Case 12: Order status is CONFIRMED (not yet shipped)
        if ("CONFIRMED".equalsIgnoreCase(order.getStatus())) {
            log.error("Cannot return order {} - status is CONFIRMED", order.getId());
            throw new BadRequestException("This order has not been shipped yet. Please cancel instead of return.");
        }

        // Edge Case 13: Order status is SHIPPED (not yet delivered)
        if ("SHIPPED".equalsIgnoreCase(order.getStatus())) {
            log.error("Cannot return order {} - status is SHIPPED", order.getId());
            throw new BadRequestException("This order has not been delivered yet. Please wait for delivery before requesting return.");
        }

        // Edge Case 14: Order status is CANCELLED
        if ("CANCELLED".equalsIgnoreCase(order.getStatus())) {
            log.error("Cannot return order {} - status is CANCELLED", order.getId());
            throw new BadRequestException("Cannot return a cancelled order");
        }

        // Edge Case 15: Order status is not DELIVERED
        if (!"DELIVERED".equalsIgnoreCase(order.getStatus())) {
            log.error("Cannot return order {} - status is {}", order.getId(), order.getStatus());
            throw new BadRequestException("Return is only allowed for delivered orders. Current status: " + order.getStatus());
        }

        // Note: Return window validation would require a deliveredAt field in Order model
        // For now, we allow returns for any DELIVERED order
        log.info("Order {} is DELIVERED, proceeding with return request", order.getId());

        // ─────────────────────────────────────────────────────────────────────
        // CHECK EXISTING RETURN REQUEST
        // ─────────────────────────────────────────────────────────────────────
        
        // Edge Case 17: Return already requested
        if (returnRepository.existsByOrderId(order.getId())) {
            log.error("Return already requested for order: {}", order.getId());
            throw new BadRequestException("A return request already exists for this order");
        }

        // ─────────────────────────────────────────────────────────────────────
        // CREATE RETURN REQUEST
        // ─────────────────────────────────────────────────────────────────────
        
        ReturnRequest returnRequest = new ReturnRequest();
        returnRequest.setOrderId(order.getId());
        returnRequest.setReason(requestDto.getReason().trim());
        returnRequest.setStatus("REQUESTED");
        returnRequest.setCreatedAt(LocalDateTime.now());
        
        returnRepository.save(returnRequest);
        
        log.info("Return request created successfully for order: {}", order.getId());
    }

    // ════════════════════════════════════════════════════════════════════════
    // GET USER'S RETURN REQUESTS
    // ════════════════════════════════════════════════════════════════════════
    
    @Override
    public List<ReturnRequest> getUserReturnRequests(Long userId) {
        log.info("Fetching return requests for user: {}", userId);
        
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
        
        List<ReturnRequest> requests = returnRepository.findByUserId(userId);
        
        // Handle null result
        if (requests == null) {
            requests = Collections.emptyList();
        }
        
        log.info("Found {} return requests for user: {}", requests.size(), userId);
        return requests;
    }
    
 // ════════════════════════════════════════════════════════════════════════
 // GET RETURN STATUS FOR SPECIFIC ORDER
 // ════════════════════════════════════════════════════════════════════════

 @Override
 public ReturnRequest getReturnByOrderId(Long userId, Long orderId) {
     log.info("Fetching return request - User: {}, Order: {}", userId, orderId);
     
     // ─────────────────────────────────────────────────────────────────────
     // INPUT VALIDATION
     // ─────────────────────────────────────────────────────────────────────
     
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
     
     // Edge Case 3: Null order ID
     if (orderId == null) {
         log.error("Order ID is null");
         throw new BadRequestException("Order ID is required");
     }
     
     // Edge Case 4: Invalid order ID
     if (orderId <= 0) {
         log.error("Invalid order ID: {}", orderId);
         throw new BadRequestException("Invalid order ID");
     }
     
     // ─────────────────────────────────────────────────────────────────────
     // ORDER VALIDATION
     // ─────────────────────────────────────────────────────────────────────
     
     // Edge Case 5: Order not found
     Order order = orderRepository.findById(orderId)
             .orElseThrow(() -> {
                 log.error("Order not found: {}", orderId);
                 return new ResourceNotFoundException("Order not found with ID: " + orderId);
             });
     
     // Edge Case 6: Order doesn't belong to user (Authorization)
     if (!order.getUserId().equals(userId)) {
         log.error("Unauthorized access. User {} trying to view return for order of user {}", 
                 userId, order.getUserId());
         throw new UnauthorizedException("You are not authorized to view this return request");
     }
     
     // ─────────────────────────────────────────────────────────────────────
     // FETCH RETURN REQUEST
     // ─────────────────────────────────────────────────────────────────────
     
     // Edge Case 7: No return request found for this order
     ReturnRequest returnRequest = returnRepository.findByOrderId(orderId)
             .orElseThrow(() -> {
                 log.error("No return request found for order: {}", orderId);
                 return new ResourceNotFoundException("No return request found for order ID: " + orderId);
             });
     
     log.info("Found return request for order: {}, status: {}", orderId, returnRequest.getStatus());
     return returnRequest;
 }
}