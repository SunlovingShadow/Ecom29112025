package com.ecommerce.repository.rowmapper;

import com.ecommerce.enums.DiscountType;
import com.ecommerce.model.Coupon;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CouponRowMapper implements RowMapper<Coupon> {

    @Override
    public Coupon mapRow(ResultSet rs, int rowNum) throws SQLException {
        Coupon coupon = new Coupon();

        coupon.setId(rs.getLong("id"));
        coupon.setCode(rs.getString("code"));

        // Enum Conversion
        String typeStr = rs.getString("discount_type");
        if (typeStr != null) {
            coupon.setDiscountType(DiscountType.valueOf(typeStr));
        }

        // *** FIX: Use getBigDecimal instead of getDouble ***
        coupon.setDiscountValue(rs.getBigDecimal("discount_value"));
        coupon.setMinOrderAmount(rs.getBigDecimal("min_order_amount"));

        // Date Conversion
        java.sql.Date from = rs.getDate("valid_from");
        java.sql.Date to = rs.getDate("valid_to");

        coupon.setValidFrom(from != null ? from.toLocalDate() : null);
        coupon.setValidTo(to != null ? to.toLocalDate() : null);

        // Shop ID (Handle Null for Global Coupons)
        long shopId = rs.getLong("shop_id");
        if (!rs.wasNull()) {
            coupon.setShopId(shopId);
        } else {
            coupon.setShopId(null);
        }

        coupon.setActive(rs.getBoolean("is_active"));

        return coupon;
    }
}