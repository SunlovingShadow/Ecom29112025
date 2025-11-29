package com.ecommerce.service;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.CouponRequest;
import com.ecommerce.dto.CouponResponse;
import com.ecommerce.model.Coupon;
import java.util.List;

public interface CouponService {

    // --- SNIGDHA'S METHODS (Admin/Shopkeeper) ---
    CouponResponse createCoupon(CouponRequest request);

    CouponResponse updateCoupon(Long id, CouponRequest request);
    
    boolean deleteCoupon(Long id);

    CouponResponse getCouponById(Long id);

    List<CouponResponse> getCouponsByShop(Long shopId);

    List<CouponResponse> getAllCoupons();

    // --- SAMADRITA'S METHODS (User/Checkout) ---
    // You MUST have this, or your Order Logic cannot validate coupons!
    ApiResponse<Coupon> validateCoupon(String code, Long userId, Double cartTotal);
}