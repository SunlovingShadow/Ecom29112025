package com.ecommerce.repository.rowmapper;

import com.ecommerce.model.CartItem;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CartItemRowMapper implements RowMapper<CartItem> {
    @Override
    public CartItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        CartItem item = new CartItem();
        // Map physical table columns
        item.setId(rs.getLong("id"));
        item.setCartId(rs.getLong("cart_id"));
        item.setProductId(rs.getLong("product_id"));
        item.setQuantity(rs.getInt("quantity"));
        item.setPriceAtAdd(rs.getBigDecimal("price_at_add"));

        // Map JOINed columns (if they exist in the query)
        try {
            if(rs.findColumn("product_name") > 0) item.setProductName(rs.getString("product_name"));
            if(rs.findColumn("current_price") > 0) item.setCurrentPrice(rs.getBigDecimal("current_price"));
            if(rs.findColumn("shop_id") > 0) item.setShopId(rs.getLong("shop_id"));
            if(rs.findColumn("shop_name") > 0) item.setShopName(rs.getString("shop_name"));
        } catch (SQLException e) {
            // Columns might not be in the select statement, ignore
        }
        return item;
    }
}