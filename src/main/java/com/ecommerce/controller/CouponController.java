package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.CouponRequest;
import com.ecommerce.dto.CouponResponse;
import com.ecommerce.model.Coupon;
import com.ecommerce.service.CouponService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private static final Logger log = LoggerFactory.getLogger(CouponController.class);

    private final CouponService couponService;

    // Constructor Injection
    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    // ════════════════════════════════════════════════════════════════════════
    // ADMIN/SHOPKEEPER ENDPOINTS (Snigdha's)
    // ════════════════════════════════════════════════════════════════════════

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CouponResponse>> createCoupon(@RequestBody CouponRequest request) {
        log.info("POST /api/coupons/create - Creating coupon: {}", 
                request != null ? request.getCode() : "NULL");
        
        CouponResponse coupon = couponService.createCoupon(request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Coupon created successfully", coupon));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponResponse>> updateCoupon(
            @PathVariable Long id, 
            @RequestBody CouponRequest request) {
        
        log.info("PUT /api/coupons/{} - Updating coupon", id);
        
        CouponResponse coupon = couponService.updateCoupon(id, request);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Coupon updated successfully", coupon));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(@PathVariable Long id) {
        log.info("DELETE /api/coupons/{} - Deleting coupon", id);
        
        couponService.deleteCoupon(id);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Coupon deactivated successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponResponse>> getCouponById(@PathVariable Long id) {
        log.info("GET /api/coupons/{} - Fetching coupon", id);
        
        CouponResponse coupon = couponService.getCouponById(id);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Coupon retrieved successfully", coupon));
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getCouponsByShop(@PathVariable Long shopId) {
        log.info("GET /api/coupons/shop/{} - Fetching shop coupons", shopId);
        
        List<CouponResponse> coupons = couponService.getCouponsByShop(shopId);
        
        String message = coupons.isEmpty() 
                ? "No coupons found for this shop" 
                : "Found " + coupons.size() + " coupon(s)";
        
        return ResponseEntity.ok(new ApiResponse<>(true, message, coupons));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CouponResponse>>> listAllCoupons() {
        log.info("GET /api/coupons/all - Fetching all coupons");
        
        List<CouponResponse> coupons = couponService.getAllCoupons();
        
        String message = coupons.isEmpty() 
                ? "No coupons found" 
                : "Found " + coupons.size() + " coupon(s)";
        
        return ResponseEntity.ok(new ApiResponse<>(true, message, coupons));
    }

    // ════════════════════════════════════════════════════════════════════════
    // USER ENDPOINTS (Samadrita's)
    // ════════════════════════════════════════════════════════════════════════

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Coupon>> validateCoupon(
            @RequestParam String code, 
            @RequestParam Long userId,
            @RequestParam Double cartTotal) {
        
        log.info("GET /api/coupons/validate - Validating coupon: {} for user: {} with cart: {}", 
                code, userId, cartTotal);
        
        ApiResponse<Coupon> response = couponService.validateCoupon(code, userId, cartTotal);
        
        return ResponseEntity.ok(response);
    }

    // ════════════════════════════════════════════════════════════════════════
    // ADDITIONAL HELPFUL ENDPOINTS
    // ════════════════════════════════════════════════════════════════════════

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getActiveCoupons() {
        log.info("GET /api/coupons/active - Fetching active coupons");
        
        List<CouponResponse> allCoupons = couponService.getAllCoupons();
        
        // Filter for active coupons only
        List<CouponResponse> activeCoupons = allCoupons.stream()
                .filter(CouponResponse::isActive)
                .collect(java.util.stream.Collectors.toList());
        
        String message = activeCoupons.isEmpty() 
                ? "No active coupons available" 
                : "Found " + activeCoupons.size() + " active coupon(s)";
        
        return ResponseEntity.ok(new ApiResponse<>(true, message, activeCoupons));
    }
}