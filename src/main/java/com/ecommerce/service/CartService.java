package com.ecommerce.service;

import com.ecommerce.dto.CartResponse;
import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;

public interface CartService {
   
    Cart getOrCreateCart(Long userId);
    
   
    CartResponse getUserCart(Long userId);
   
    CartItem addToCart(Long userId, Long productId, Integer quantity);
    
    
    CartItem updateCartItemQuantity(Long userId, Long cartItemId, Integer quantity);
  
    void removeFromCart(Long userId, Long cartItemId);
    
    void clearCart(Long userId);
    
    CartResponse validateCart(Long userId);
}