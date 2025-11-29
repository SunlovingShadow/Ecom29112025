package com.ecommerce.controller;

import com.ecommerce.dto.CouponRequest;
import com.ecommerce.dto.CouponResponse;
import com.ecommerce.dto.ShopResponse;
import com.ecommerce.dto.UserResponse;
import com.ecommerce.repository.AdminLogsRepository;
import com.ecommerce.service.AdminService;
import com.ecommerce.service.impl.DtoMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;    

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
	private  AdminService adminService;

    @Autowired
    private AdminLogsRepository adminLogsRepository;
    
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(@RequestParam Long adminUserId) {
        return ResponseEntity.ok(adminService.getAllUsers(adminUserId));
    }
    
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUserById(@RequestParam Long adminUserId,@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getUserById(adminUserId,userId));
    }
    
    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<?> updateUserStatus(@RequestParam Long adminUserId, @PathVariable Long userId, @RequestParam String status) {
        return ResponseEntity.ok(adminService.updateUserStatus(adminUserId, userId, status));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@RequestParam Long adminUserId, @PathVariable Long userId) {
        adminService.deleteUser(adminUserId, userId);
    	return ResponseEntity.ok("User deleted successfully");
    }

    @GetMapping("/shops")
    public ResponseEntity<List<ShopResponse>> getAllShops(@RequestParam Long adminUserId) {
        return ResponseEntity.ok(adminService.getAllShops(adminUserId));
    }
    
    @GetMapping("/shops/{shopId}")
    public ResponseEntity<ShopResponse> getShopById(@RequestParam Long adminUserId,@PathVariable Long shopId) {
        return ResponseEntity.ok(adminService.getShopById(adminUserId,shopId));
    }
    
    @GetMapping("/shops/pending")
    public ResponseEntity<List<ShopResponse>> getPendingShops(@RequestParam Long adminUserId) {
        return ResponseEntity.ok(adminService.getPendingShops(adminUserId));
    }


    @PatchMapping("/shops/{shopId}/approve")
    public ResponseEntity<?> approveShop(@RequestParam Long adminUserId, @PathVariable Long shopId) {
        return ResponseEntity.ok(adminService.approveShop(adminUserId, shopId));
    }

    @PatchMapping("/shops/{shopId}/reject")
    public ResponseEntity<?> rejectShop(@RequestParam Long adminUserId, @PathVariable Long shopId) {
        return ResponseEntity.ok(adminService.rejectShop(adminUserId, shopId));
    }

    @DeleteMapping("/shops/{shopId}")
    public ResponseEntity<?> deleteShop(@RequestParam Long adminUserId, @PathVariable Long shopId) {
        return ResponseEntity.ok(adminService.softDeleteShop(adminUserId, shopId));
    }

    @PostMapping("/coupons")
    public ResponseEntity<CouponResponse> createCoupon(@RequestParam Long adminUserId, @RequestBody CouponRequest req) {
        return ResponseEntity.ok(adminService.createCoupon(adminUserId, req));
    }

    @PutMapping("/coupons/{id}")
    public ResponseEntity<CouponResponse> updateCoupon(@RequestParam Long adminUserId, @PathVariable Long id, @RequestBody CouponRequest req) {
        return ResponseEntity.ok(adminService.updateCoupon(adminUserId, id, req));
    }

    @DeleteMapping("/coupons/{id}")
    public ResponseEntity<?> deleteCoupon(@RequestParam Long adminUserId, @PathVariable Long id) {
        return ResponseEntity.ok(adminService.deleteCoupon(adminUserId, id));
    }

    @GetMapping("/coupons")
    public ResponseEntity<List<CouponResponse>> listCoupons(@RequestParam Long adminUserId) {
        return ResponseEntity.ok(adminService.getAllCoupons(adminUserId));
    }
    
    @GetMapping("/logs")
    public ResponseEntity<?> getLogs(@RequestParam(defaultValue="20") int limit) {
        return ResponseEntity.ok(
        		adminLogsRepository.findRecent(limit)
        		.stream()
        		.map(DtoMapper::adminLogToResponse)
        		.toList()
        		);
    }
}