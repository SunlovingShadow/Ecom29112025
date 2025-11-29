package com.ecommerce.repository;

import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;
import java.util.List;
import java.util.Optional;


public interface CartRepository {
    
    Optional<Cart> findByUserId(Long userId);
  
    Optional<Cart> findById(Long cartId);

    Cart create(Long userId);
    
    void updateTimestamp(Long cartId);
    
    void delete(Long cartId);
    
    List<CartItem> findItemsByCartId(Long cartId);
    
    Optional<CartItem> findCartItem(Long cartId, Long productId);
   
    CartItem addItem(CartItem cartItem);
    
    void updateItemQuantity(Long cartItemId, Integer quantity);
    
    void removeItem(Long cartItemId);
    
    void clearCart(Long cartId);
    
    int getItemsCount(Long cartId);
    
    Optional<CartItem> findItemById(Long cartItemId);
}