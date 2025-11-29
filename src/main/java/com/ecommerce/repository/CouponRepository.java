package com.ecommerce.repository;

import com.ecommerce.model.Coupon;
import java.util.List;
import java.util.Optional;

public interface CouponRepository {

    // --- SNIGDHA'S METHODS (Management) ---
    Long save(Coupon coupon);
    Coupon update(Coupon coupon);
    Optional<Coupon> findById(Long id);
    List<Coupon> findByShopId(Long shopId);
    boolean softDelete(Long id);
    List<Coupon> findAll();

    // --- SHARED METHOD ---
    Optional<Coupon> findByCode(String code);

    // --- SAMADRITA'S METHODS (Usage Logic) ---
    void recordUsage(Long userId, Long couponId, Long orderId);
    boolean isUsedByUser(Long userId, Long couponId);
}