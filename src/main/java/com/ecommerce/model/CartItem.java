package com.ecommerce.model;

import java.math.BigDecimal;

public class CartItem {
    // Database Columns
    private Long id;
    private Long cartId;
    private Long productId;
    private Integer quantity;
    private BigDecimal priceAtAdd;

    // Enriched Data (Fetched via SQL Joins for display)
    private String productName;
    private BigDecimal currentPrice; // Real-time price from products table
    private Long shopId;
    private String shopName;

    public CartItem() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCartId() { return cartId; }
    public void setCartId(Long cartId) { this.cartId = cartId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getPriceAtAdd() { return priceAtAdd; }
    public void setPriceAtAdd(BigDecimal priceAtAdd) { this.priceAtAdd = priceAtAdd; }
    
    // Extra Details
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }
    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }
}