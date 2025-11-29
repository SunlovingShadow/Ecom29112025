package com.ecommerce.dto;

import java.math.BigDecimal;

public class OrderItemResponse {
    private Long productId;
    private String productName; 
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    // Constructors
    public OrderItemResponse() {}
    
    public OrderItemResponse(Long productId, String productName, Integer quantity, BigDecimal unitPrice, BigDecimal totalPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    // Getters & Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
}