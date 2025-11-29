package com.ecommerce.repository;

import com.ecommerce.dto.OrderItemResponse; // Make sure this import is here
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    void saveOrderItems(List<OrderItem> items);
    List<Order> findByUserId(Long userId);
    Optional<Order> findById(Long orderId);
    void updatePaymentStatus(Long orderId, String paymentStatus, String orderStatus);
    
    // --- NEW METHODS ---
    List<OrderItemResponse> findItemsByOrderId(Long orderId);
    void updateOrderStatus(Long orderId, String status);
    
    // FOR ADMIN: Get all orders (Newest first)
    List<Order> findAll();
}