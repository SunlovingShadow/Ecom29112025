package com.ecommerce.model;

import java.time.LocalDateTime;

public class ReturnRequest {
    private Long id;
    private Long orderId;
    private String reason;
    private String status; // REQUESTED, APPROVED, REJECTED, REFUNDED
    private LocalDateTime createdAt;

    public ReturnRequest() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}