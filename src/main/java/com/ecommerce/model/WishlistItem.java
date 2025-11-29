package com.ecommerce.model;

import java.time.LocalDateTime;

public class WishlistItem {
    private Long id;
    private Long userId;
    private Long productId;
    private LocalDateTime addedAt;

    public WishlistItem() {}

    public WishlistItem(Long id, Long userId, Long productId, LocalDateTime addedAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.addedAt = addedAt;
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
}