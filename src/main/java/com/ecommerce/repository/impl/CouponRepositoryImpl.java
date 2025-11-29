package com.ecommerce.repository.impl;

import com.ecommerce.model.Coupon;
import com.ecommerce.repository.CouponRepository;
import com.ecommerce.repository.rowmapper.CouponRowMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class CouponRepositoryImpl implements CouponRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final CouponRowMapper mapper = new CouponRowMapper();

    // ======================================================================
    // SNIGDHA'S METHODS (Admin Management)
    // ======================================================================

    @Override
    public Long save(Coupon c) {
        String sql = "INSERT INTO coupons(code, discount_type, discount_value, min_order_amount, valid_from, valid_to, shop_id, is_active) VALUES (?,?,?,?,?,?,?,?)";
        jdbcTemplate.update(sql,
                c.getCode(),
                c.getDiscountType().name(),
                c.getDiscountValue(),
                c.getMinOrderAmount(),
                Date.valueOf(c.getValidFrom()),
                Date.valueOf(c.getValidTo()),
                c.getShopId(),
                c.isActive()
        );
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    @Override
    public Coupon update(Coupon c) {
        String sql = "UPDATE coupons SET code=?, discount_type=?, discount_value=?, min_order_amount=?, valid_from=?, valid_to=?, is_active=? WHERE id=?";
        jdbcTemplate.update(sql,
                c.getCode(),
                c.getDiscountType().name(),
                c.getDiscountValue(),
                c.getMinOrderAmount(),
                Date.valueOf(c.getValidFrom()),
                Date.valueOf(c.getValidTo()),
                c.isActive(),
                c.getId());
        return findById(c.getId()).orElse(null);
    }

    @Override
    public Optional<Coupon> findById(Long id) {
        String sql = "SELECT * FROM coupons WHERE id=? AND is_active=1";
        List<Coupon> list = jdbcTemplate.query(sql, mapper, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public Optional<Coupon> findByCode(String code) {
        String sql = "SELECT * FROM coupons WHERE code=? AND is_active=1";
        List<Coupon> list = jdbcTemplate.query(sql, mapper, code);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public List<Coupon> findByShopId(Long shopId) {
        String sql = "SELECT * FROM coupons WHERE shop_id=? AND is_active=1";
        return jdbcTemplate.query(sql, mapper, shopId);
    }

    @Override
    public boolean softDelete(Long id) {
        String sql = "UPDATE coupons SET is_active=0 WHERE id=?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    @Override
    public List<Coupon> findAll() {
        String sql = "SELECT * FROM coupons WHERE is_active=1";
        return jdbcTemplate.query(sql, mapper);
    }

    // ======================================================================
    // SAMADRITA'S METHODS (User Usage Logic)
    // ======================================================================

    @Override
    public void recordUsage(Long userId, Long couponId, Long orderId) {
        String sql = "INSERT INTO coupon_usage (user_id, coupon_id, order_id, used_at) VALUES (?, ?, ?, NOW())";
        jdbcTemplate.update(sql, userId, couponId, orderId);
    }

    @Override
    public boolean isUsedByUser(Long userId, Long couponId) {
        String sql = "SELECT COUNT(*) FROM coupon_usage WHERE user_id = ? AND coupon_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, couponId);
        return count != null && count > 0;
    }
}