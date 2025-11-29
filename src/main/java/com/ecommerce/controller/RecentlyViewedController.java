package com.ecommerce.controller;

import com.ecommerce.dto.RecentlyViewedResponse;
import com.ecommerce.service.RecentlyViewedService;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing user's recently viewed products.
 */
@RestController
@RequestMapping("/api/recently-viewed")
public class RecentlyViewedController {

    private final RecentlyViewedService recentlyViewedService;

    public RecentlyViewedController(RecentlyViewedService recentlyViewedService) {
        this.recentlyViewedService = recentlyViewedService;
    }

    // ------------------------------------------------------------
    // ADD RECENTLY VIEWED PRODUCT
    // ------------------------------------------------------------
    @PostMapping("/add")
    public ResponseEntity<String> addViewedProduct(
            @RequestParam Long userId,
            @RequestParam Long productId
    ) {
        recentlyViewedService.addViewedProduct(userId, productId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Product added to recently viewed.");
    }

    // ------------------------------------------------------------
    // GET LAST N RECENTLY VIEWED PRODUCTS
    // ------------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<RecentlyViewedResponse>> getRecentlyViewed(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<RecentlyViewedResponse> list =
                recentlyViewedService.getRecentlyViewed(userId, limit);

        return ResponseEntity.ok(list);
    }

    // ------------------------------------------------------------
    // CLEAR ALL RECENTLY VIEWED
    // ------------------------------------------------------------
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearRecentlyViewed(@RequestParam Long userId) {

        boolean cleared = recentlyViewedService.clearRecentlyViewed(userId);

        if (cleared) {
            return ResponseEntity.ok("Recently viewed history cleared.");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Failed to clear recently viewed history.");
    }
}