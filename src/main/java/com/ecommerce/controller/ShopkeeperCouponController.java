package com.ecommerce.controller;

import com.ecommerce.dto.CouponRequest;
import com.ecommerce.dto.CouponResponse;
import com.ecommerce.service.CouponService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/shopkeeper/coupons")
public class ShopkeeperCouponController {

    @Autowired
	private  CouponService couponService;

    @PostMapping
    public ResponseEntity<CouponResponse> create(@RequestBody CouponRequest req) {
        return ResponseEntity.ok(couponService.createCoupon(req));
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<CouponResponse>> getByShop(@PathVariable Long shopId) {
        return ResponseEntity.ok(couponService.getCouponsByShop(shopId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CouponResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(couponService.getCouponById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CouponResponse> update(@PathVariable Long id, @RequestBody CouponRequest req) {
        return ResponseEntity.ok(couponService.updateCoupon(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return ResponseEntity.ok(couponService.deleteCoupon(id));
    }
}