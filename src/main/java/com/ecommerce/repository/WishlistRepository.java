package com.ecommerce.repository;

import com.ecommerce.model.WishlistItem;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository {

    WishlistItem add(Long userId, Long productId);

    boolean remove(Long userId, Long productId);

    List<WishlistItem> findByUserId(Long userId);

    Optional<WishlistItem> findItem(Long userId, Long productId);
    
}