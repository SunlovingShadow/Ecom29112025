package com.ecommerce.controller;

import com.ecommerce.dto.ShopRequest;
import com.ecommerce.dto.ShopResponse;
import com.ecommerce.service.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
public class ShopController {

    private final ShopService service;
    public ShopController(ShopService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<ShopResponse> create(@RequestBody ShopRequest req) {
        return ResponseEntity.ok(service.createShop(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShopResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getShopById(id));
    }

    @GetMapping
    public ResponseEntity<List<ShopResponse>> getAll() {
        return ResponseEntity.ok(service.getAllShops());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShopResponse> update(@PathVariable Long id, @RequestBody ShopRequest req) {
        return ResponseEntity.ok(service.updateShop(id, req));
    }
    
    @DeleteMapping("/{shopId}")
    public ResponseEntity<?> deleteShop(@PathVariable Long shopId) {
        return ResponseEntity.ok(service.softDeleteShop(shopId));
    }
}