package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.CartRequest;
import com.ecommerce.dto.CartResponse;
import com.ecommerce.model.CartItem;
import com.ecommerce.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/cart")
public class CartController {

    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    private final CartService cartService;

    // Constructor Injection
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

 
    
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<CartResponse>> getCartByUserId(@PathVariable Long userId) {
        log.info("GET /api/cart/{} - Fetching cart", userId);
        
        CartResponse cartResponse = cartService.getUserCart(userId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Cart retrieved successfully", cartResponse));
    }

    // ════════════════════════════════════════════════════════════════════════
    // ADD ITEM TO CART
    // ════════════════════════════════════════════════════════════════════════
    
    @PostMapping("/{userId}/items")
    public ResponseEntity<ApiResponse<CartItem>> addItemToCart(
            @PathVariable Long userId, 
            @RequestBody CartRequest cartRequest) {
        
        log.info("POST /api/cart/{}/items - Adding item: productId={}, quantity={}", 
                userId, cartRequest.getProductId(), cartRequest.getQuantity());
        
        CartItem cartItem = cartService.addToCart(
                userId, 
                cartRequest.getProductId(), 
                cartRequest.getQuantity()
        );
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Item added to cart successfully", cartItem));
    }

    // ════════════════════════════════════════════════════════════════════════
    // UPDATE ITEM QUANTITY
    // ════════════════════════════════════════════════════════════════════════
    
    @PutMapping("/{userId}/items/{itemId}")
    public ResponseEntity<ApiResponse<CartItem>> updateCartItemQuantity(
            @PathVariable Long userId, 
            @PathVariable Long itemId, 
            @RequestBody CartRequest cartRequest) {
        
        log.info("PUT /api/cart/{}/items/{} - Updating quantity to {}", 
                userId, itemId, cartRequest.getQuantity());
        
        CartItem updatedItem = cartService.updateCartItemQuantity(
                userId, 
                itemId, 
                cartRequest.getQuantity()
        );
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Cart item updated successfully", updatedItem));
    }

    // ════════════════════════════════════════════════════════════════════════
    // REMOVE ITEM FROM CART
    // ════════════════════════════════════════════════════════════════════════
    
    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<ApiResponse<Void>> removeItemFromCart(
            @PathVariable Long userId, 
            @PathVariable Long itemId) {
        
        log.info("DELETE /api/cart/{}/items/{} - Removing item", userId, itemId);
        
        cartService.removeFromCart(userId, itemId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Item removed from cart successfully"));
    }

    // ════════════════════════════════════════════════════════════════════════
    // CLEAR ENTIRE CART
    // ════════════════════════════════════════════════════════════════════════
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> clearCart(@PathVariable Long userId) {
        log.info("DELETE /api/cart/{} - Clearing cart", userId);
        
        cartService.clearCart(userId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Cart cleared successfully"));
    }

    // ════════════════════════════════════════════════════════════════════════
    // VALIDATE CART (Before Checkout)
    // ════════════════════════════════════════════════════════════════════════
    
    @GetMapping("/{userId}/validate")
    public ResponseEntity<ApiResponse<CartResponse>> validateCart(@PathVariable Long userId) {
        log.info("GET /api/cart/{}/validate - Validating cart", userId);
        
        CartResponse cartResponse = cartService.validateCart(userId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Cart is valid for checkout", cartResponse));
    }
}