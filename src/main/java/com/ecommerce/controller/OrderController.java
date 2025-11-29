package com.ecommerce.controller;

import com.ecommerce.dto.OrderRequest;
import com.ecommerce.dto.OrderResponse;
import com.ecommerce.model.Order;
import com.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 1. Place Order
    @PostMapping("/checkout/{userId}")
    public ResponseEntity<List<Order>> checkout(@PathVariable Long userId, @RequestBody OrderRequest request) {
        List<Order> orders = orderService.placeOrder(userId, request);
        return ResponseEntity.ok(orders);
    }

    // 2. Get Order History for User (NEW)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@PathVariable Long userId) {
        List<OrderResponse> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }

    // 3. Get Specific Order Details (NEW)
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable Long orderId) {
        OrderResponse order = orderService.getOrderDetails(orderId);
        return ResponseEntity.ok(order);
    }

    // 4. Cancel Order (NEW)
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok("Order cancelled successfully");
    }
    
 // ... existing code ...

 // --------------------------------------------------------
 // ADMIN ENDPOINTS
 // --------------------------------------------------------

 // 5. ADMIN: View All Orders
 @GetMapping("/admin/all")
 public ResponseEntity<List<OrderResponse>> getAllOrders() {
     List<OrderResponse> orders = orderService.getAllOrders();
     return ResponseEntity.ok(orders);
 }

 // 6. ADMIN: Update Order Status (e.g., /api/orders/admin/101/status?status=SHIPPED)
 @PatchMapping("/admin/{orderId}/status")
 public ResponseEntity<OrderResponse> updateOrderStatus(
         @PathVariable Long orderId, 
         @RequestParam String status) {
     
     OrderResponse updatedOrder = orderService.updateOrderStatus(orderId, status);
     return ResponseEntity.ok(updatedOrder);
 }
}