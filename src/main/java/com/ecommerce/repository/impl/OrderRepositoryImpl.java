package com.ecommerce.repository.impl;

import com.ecommerce.dto.OrderItemResponse;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    @Autowired
    private JdbcTemplate jdbc;

    // ========================================================================
    // 1. SAVE ORDER HEADER
    // ========================================================================
    @Override
    public Order save(Order order) {
        String sql = """
            INSERT INTO orders (
                user_id, shop_id, order_number, total_amount, 
                status, payment_status, shipping_address, 
                order_parent_id, created_at
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, order.getUserId());
            ps.setLong(2, order.getShopId());
            ps.setString(3, order.getOrderNumber());
            ps.setBigDecimal(4, order.getTotalAmount());
            ps.setString(5, "PLACED");      // Default Status
            ps.setString(6, "PENDING");     // Default Payment Status
            ps.setString(7, order.getShippingAddress());
            
            // Handle nullable parent ID (For split orders)
            if (order.getOrderParentId() != null) {
                ps.setLong(8, order.getOrderParentId());
            } else {
                ps.setNull(8, java.sql.Types.BIGINT);
            }
            
            return ps;
        }, keyHolder);

        // Set the generated ID back to the object
        order.setId(keyHolder.getKey().longValue());
        return order;
    }

    // ========================================================================
    // 2. SAVE ORDER ITEMS (BATCH INSERT)
    // ========================================================================
    @Override
    public void saveOrderItems(List<OrderItem> items) {
        String sql = """
            INSERT INTO order_items (order_id, product_id, quantity, unit_price, total_price) 
            VALUES (?, ?, ?, ?, ?)
        """;
        
        // Efficient Batch Insert
        jdbc.batchUpdate(sql, items, items.size(), (ps, item) -> {
            ps.setLong(1, item.getOrderId());
            ps.setLong(2, item.getProductId());
            ps.setInt(3, item.getQuantity());
            ps.setBigDecimal(4, item.getUnitPrice());
            ps.setBigDecimal(5, item.getTotalPrice());
        });
    }

    // ========================================================================
    // 3. FIND ORDERS BY USER ID (History)
    // ========================================================================
    @Override
    public List<Order> findByUserId(Long userId) {
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC";
        return jdbc.query(sql, orderRowMapper, userId);
    }

    // ========================================================================
    // 4. FIND ORDER BY ID
    // ========================================================================
    @Override
    public Optional<Order> findById(Long orderId) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try {
            Order order = jdbc.queryForObject(sql, orderRowMapper, orderId);
            return Optional.ofNullable(order);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // ========================================================================
    // 5. UPDATE PAYMENT STATUS (After successful payment)
    // ========================================================================
    @Override
    public void updatePaymentStatus(Long orderId, String paymentStatus, String orderStatus) {
        String sql = "UPDATE orders SET payment_status = ?, status = ? WHERE id = ?";
        jdbc.update(sql, paymentStatus, orderStatus, orderId);
    }

    // ========================================================================
    // 6. FIND ITEMS FOR ORDER (For Detail View)
    // Joins with Product table to get Product Names
    // ========================================================================
    @Override
    public List<OrderItemResponse> findItemsByOrderId(Long orderId) {
        String sql = """
            SELECT oi.product_id, p.name as product_name, 
                   oi.quantity, oi.unit_price, oi.total_price
            FROM order_items oi
            JOIN products p ON oi.product_id = p.id
            WHERE oi.order_id = ?
        """;

        return jdbc.query(sql, (rs, rowNum) -> {
            return new OrderItemResponse(
                rs.getLong("product_id"),
                rs.getString("product_name"),
                rs.getInt("quantity"),
                rs.getBigDecimal("unit_price"),
                rs.getBigDecimal("total_price")
            );
        }, orderId);
    }

    // ========================================================================
    // 7. UPDATE ORDER STATUS (For Cancellation/Admin)
    // ========================================================================
    @Override
    public void updateOrderStatus(Long orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        jdbc.update(sql, status, orderId);
    }
    
    // ... existing methods ...

    @Override
    public List<Order> findAll() {
        String sql = "SELECT * FROM orders ORDER BY created_at DESC";
        return jdbc.query(sql, orderRowMapper);
    }
    
    
    

    // ========================================================================
    // ROW MAPPER (Helper)
    // ========================================================================
    private final RowMapper<Order> orderRowMapper = (rs, rowNum) -> {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setUserId(rs.getLong("user_id"));
        order.setShopId(rs.getLong("shop_id"));
        order.setOrderNumber(rs.getString("order_number"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setStatus(rs.getString("status"));
        order.setPaymentStatus(rs.getString("payment_status")); // Added this
        order.setShippingAddress(rs.getString("shipping_address"));
        
        long parentId = rs.getLong("order_parent_id");
        if (!rs.wasNull()) {
            order.setOrderParentId(parentId);
        }
        
        if (rs.getTimestamp("created_at") != null) {
            order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        return order;
    };
}