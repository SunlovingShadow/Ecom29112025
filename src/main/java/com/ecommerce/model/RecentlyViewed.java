package com.ecommerce.model;

import java.time.LocalDateTime;

/**
 * Entity representing user's recently viewed products.
 */
public class RecentlyViewed {

    private Long id;               // Primary key
    private Long userId;           // Which user viewed the product
    private Long productId;        // Which product was viewed
    private LocalDateTime viewedAt; // When the product was viewed

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