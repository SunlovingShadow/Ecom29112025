package com.ecommerce.repository.rowmapper;

import com.ecommerce.model.Cart;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class CartRowMapper implements RowMapper<Cart> {
    @Override
    public Cart mapRow(ResultSet rs, int rowNum) throws SQLException {
        Cart cart = new Cart();
        cart.setId(rs.getLong("id"));
        cart.setUserId(rs.getLong("user_id"));
        if (rs.getTimestamp("updated_at") != null) {
            cart.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        return cart;
    }
}