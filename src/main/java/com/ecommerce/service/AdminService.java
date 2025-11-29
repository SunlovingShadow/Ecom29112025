package com.ecommerce.service;

import com.ecommerce.dto.AdminLogResponse;
import com.ecommerce.dto.CouponRequest;
import com.ecommerce.dto.CouponResponse;
import com.ecommerce.dto.ShopResponse;
import com.ecommerce.dto.UserResponse;

import java.util.List;

public interface AdminService {

    List<UserResponse> getAllUsers(Long adminUserId);                   // includes suspended
    UserResponse getUserById(Long adminUserId,Long userId);
    boolean updateUserStatus(Long adminUserId, Long userId, String status); // adminUserId for logs
    void deleteUser(Long adminUserId, Long userId);

    List<ShopResponse> getAllShops(Long adminUserId);
    List<ShopResponse> getPendingShops(Long adminUserId);

    ShopResponse getShopById(Long adminUserId,Long shopId);
    boolean approveShop(Long adminUserId, Long shopId);
    boolean rejectShop(Long adminUserId, Long shopId);
    boolean softDeleteShop(Long adminUserId, Long shopId);

    // List<CouponResponse> getAllCoupons();
    CouponResponse createCoupon(Long adminUserId, CouponRequest request);
    CouponResponse updateCoupon(Long adminUserId, Long couponId, CouponRequest request);
    boolean deleteCoupon(Long adminUserId, Long couponId); // soft delete
    List<CouponResponse> getAllCoupons(Long adminUserId);
    
    // Admin logs (simple last N)
    List<AdminLogResponse> getRecentAdminLogs(int limit);
   
    // simple utility for controllers/tests to log action (if needed)
    void logAction(Long adminUserId, String action);
}