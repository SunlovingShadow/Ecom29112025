package com.ecommerce.service;

import com.ecommerce.dto.OrderRequest;
import com.ecommerce.dto.OrderResponse; // Import this!
import com.ecommerce.model.Order;
import java.util.List;

public interface OrderService {
    
    // Existing method
    List<Order> placeOrder(Long userId, OrderRequest request);
    
    // --- ADD THESE NEW METHODS TO FIX THE ERROR ---
    
    // 1. Get Order History
    List<OrderResponse> getUserOrders(Long userId);

    // 2. Get Specific Order Details
    OrderResponse getOrderDetails(Long orderId);

    // 3. Cancel Order
    void cancelOrder(Long orderId);
    
 // ... existing methods ...

 // ADMIN: Get all orders in the system
 List<OrderResponse> getAllOrders();

 // ADMIN: Update Order Status (e.g., PLACED -> SHIPPED -> DELIVERED)
 OrderResponse updateOrderStatus(Long orderId, String newStatus);
}