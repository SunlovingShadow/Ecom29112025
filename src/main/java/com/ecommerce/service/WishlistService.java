package com.ecommerce.service;

import com.ecommerce.dto.WishlistResponse;

import java.util.List;

public interface WishlistService {

    WishlistResponse addToWishlist(Long userId, Long productId);

    boolean removeFromWishlist(Long userId, Long productId);

    List<WishlistResponse> getUserWishlist(Long userId);
}