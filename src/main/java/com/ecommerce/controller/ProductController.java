package com.ecommerce.controller;

import com.ecommerce.dto.ProductRequest;
import com.ecommerce.dto.ProductResponse;
import com.ecommerce.service.ProductService;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final ProductService productService;

// constructor
	public ProductController(ProductService productService) {
		this.productService = productService;
	}

// Create product (shopkeeper)
	@PostMapping
	public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
		ProductResponse resp = productService.createProduct(request);
		return new ResponseEntity<>(resp, HttpStatus.CREATED);
	}

// Update product
	@PutMapping("/{id}")
	public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
		ProductResponse resp = productService.updateProduct(id, request);
		return ResponseEntity.ok(resp);
	}

// Get product (public) - includes inactive if admin; launcher uses service wrapper to decide
	@GetMapping("/{id}")
	public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
		Optional<ProductResponse> opt = productService.getProductById(id);
		return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

// List active products
	@GetMapping
	public ResponseEntity<List<ProductResponse>> listActive(
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "size", required = false, defaultValue = "20") int size) {
		List<ProductResponse> list = productService.searchProducts(null, null, page, size);
		return ResponseEntity.ok(list);
	}

// List shop's products (shop owner)
	@GetMapping("/shop/{shopId}")
	public ResponseEntity<List<ProductResponse>> getByShop(@PathVariable Long shopId) {
		List<ProductResponse> list = productService.getProductsByShop(shopId);
		return ResponseEntity.ok(list);
	}

// Soft delete product (mark inactive)
	@DeleteMapping("/{id}")
	public ResponseEntity<String> softDelete(@PathVariable Long id) {
		boolean ok = productService.softDeleteProduct(id);
		if (!ok) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to delete product");
		}
		return ResponseEntity.ok("Product marked inactive (soft-deleted)");
	}

// Search API
	@GetMapping("/search")
	public ResponseEntity<List<ProductResponse>> search(@RequestParam(value = "q", required = false) String q,
			@RequestParam(value = "categoryId", required = false) Long categoryId,
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "size", required = false, defaultValue = "20") int size) {

		List<ProductResponse> list = productService.searchProducts(q, categoryId, page, size);
		return ResponseEntity.ok(list);
	}
}

