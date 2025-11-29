package com.ecommerce.dto;

import java.time.LocalDateTime;

/**
 * DTO returned to the client for recently viewed products.
 */
public class RecentlyViewedResponse {

    private Long id;
    private Long userId;
    private Long productId;
    private LocalDateTime viewedAt;

    // -------- Getters & Setters --------

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public LocalDateTime getViewedAt() {
        return viewedAt;
    }
    public void setViewedAt(LocalDateTime viewedAt) {
        this.viewedAt = viewedAt;
    }
}