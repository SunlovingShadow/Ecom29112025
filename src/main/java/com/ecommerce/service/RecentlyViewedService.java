package com.ecommerce.service;

import com.ecommerce.dto.RecentlyViewedResponse;

import java.util.List;

public interface RecentlyViewedService {

    // Add a recently viewed product (auto handles update + limit)
    void addViewedProduct(Long userId, Long productId);

    // Get last N recently viewed products for a user (default 10)
    List<RecentlyViewedResponse> getRecentlyViewed(Long userId, int limit);

    // Clear all recently viewed items for a user
    boolean clearRecentlyViewed(Long userId);
}