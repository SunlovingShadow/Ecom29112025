package com.ecommerce.controller;

import com.ecommerce.dto.WishlistRequest;
import com.ecommerce.dto.WishlistResponse;
import com.ecommerce.model.CartItem;
import com.ecommerce.service.CartService;
import com.ecommerce.service.WishlistService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
	private  WishlistService wishlistService;

   @Autowired
   private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<WishlistResponse> add(
    		@RequestBody WishlistRequest request
    ) {
        return ResponseEntity.ok(wishlistService.addToWishlist(request.getUserId(), request.getProductId()));
    }

    @PostMapping("/remove")
    public ResponseEntity<Boolean> remove(
    		@RequestBody WishlistRequest request
    ) {
        return ResponseEntity.ok(wishlistService.removeFromWishlist(request.getUserId(), request.getProductId()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<WishlistResponse>> getWishlist(@PathVariable Long userId) {
        return ResponseEntity.ok(wishlistService.getUserWishlist(userId));
    }
    
    @PostMapping("/{userId}/{productId}/move-to-cart")
    	public ResponseEntity<?> moveWishlistToCart(@PathVariable Long userId, @PathVariable Long productId, @RequestParam(defaultValue="1") Integer qty){
    	
    	CartItem item=cartService.addToCart(userId,productId,qty);
    	
    	wishlistService.removeFromWishlist(userId, productId);
    	
    	return ResponseEntity.ok(item);
    	
    }
}      