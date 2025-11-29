package com.ecommerce.repository.rowmapper;

import com.ecommerce.model.Shop;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ShopRowMapper implements RowMapper<Shop> {
    @Override
    public Shop mapRow(ResultSet rs, int rowNum) throws SQLException {
        Shop s = new Shop();
        s.setId(rs.getLong("id"));
        s.setOwnerUserId(rs.getLong("owner_user_id"));
        s.setName(rs.getString("name"));
        s.setDescription(rs.getString("description"));
        s.setAddress(rs.getString("address"));
        s.setIsApproved(rs.getBoolean("is_approved"));
        s.setIsActive(rs.getBoolean("is_active"));
        Timestamp t = rs.getTimestamp("created_at");
        if (t != null) s.setCreatedAt(t.toLocalDateTime());
        return s;
    }
}