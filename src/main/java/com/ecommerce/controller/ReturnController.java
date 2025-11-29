package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.ReturnRequestDTO;
import com.ecommerce.model.ReturnRequest;
import com.ecommerce.service.ReturnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/returns")
public class ReturnController {

    private static final Logger log = LoggerFactory.getLogger(ReturnController.class);

    private final ReturnService returnService;

    // Constructor Injection
    public ReturnController(ReturnService returnService) {
        this.returnService = returnService;
    }

   
    
    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> requestReturn(
            @PathVariable Long userId, 
            @RequestBody ReturnRequestDTO request) {
        
        log.info("POST /api/returns/{} - Requesting return for order: {}", 
                userId, request != null ? request.getOrderId() : "NULL");
        
        returnService.requestReturn(userId, request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Return request submitted successfully. We will review and process it within 2-3 business days."));
    }

   
    
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<ReturnRequest>>> getUserReturns(@PathVariable Long userId) {
        log.info("GET /api/returns/{} - Fetching return requests", userId);
        
        List<ReturnRequest> requests = returnService.getUserReturnRequests(userId);
        
        String message = requests.isEmpty() 
                ? "No return requests found" 
                : "Found " + requests.size() + " return request(s)";
        
        return ResponseEntity.ok(new ApiResponse<>(true, message, requests));
    }


 // ════════════════════════════════════════════════════════════════════════
 // GET RETURN STATUS FOR SPECIFIC ORDER
 // ════════════════════════════════════════════════════════════════════════

 @GetMapping("/{userId}/order/{orderId}")
 public ResponseEntity<ApiResponse<ReturnRequest>> getReturnStatus(
         @PathVariable Long userId,
         @PathVariable Long orderId) {
     
     log.info("GET /api/returns/{}/order/{} - Fetching return status", userId, orderId);
     
     ReturnRequest returnRequest = returnService.getReturnByOrderId(userId, orderId);
     
     String message = "Return request found. Current status: " + returnRequest.getStatus();
     
     return ResponseEntity.ok(new ApiResponse<>(true, message, returnRequest));
 }
}