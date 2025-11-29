package com.ecommerce.repository.rowmapper;

import com.ecommerce.model.WishlistItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WishlistRowMapper implements RowMapper<WishlistItem> {

    @Override
    public WishlistItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        WishlistItem w = new WishlistItem();
        w.setId(rs.getLong("id"));
        w.setUserId(rs.getLong("user_id"));
        w.setProductId(rs.getLong("product_id"));
        w.setAddedAt(rs.getTimestamp("added_at").toLocalDateTime());
        return w;
    }
}