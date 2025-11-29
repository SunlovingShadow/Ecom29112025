package com.ecommerce.repository;

import com.ecommerce.model.RecentlyViewed;

import java.util.List;

public interface RecentlyViewedRepository {

    // Add or update a recently viewed product (same product â†’ update timestamp)
    void saveOrUpdate(Long userId, Long productId);

    // Fetch last N recently viewed products for a user
    List<RecentlyViewed> findLastNByUser(Long userId, int limit);

    // Delete all recently viewed items of a user
    boolean deleteAllByUser(Long userId);
}