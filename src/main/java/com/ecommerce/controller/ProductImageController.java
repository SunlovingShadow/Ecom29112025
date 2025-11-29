package com.ecommerce.controller;

import com.ecommerce.dto.ProductImageRequest;
import com.ecommerce.model.ProductImage;
import com.ecommerce.service.ProductImageService;
import com.ecommerce.dto.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Product Images
 */
@RestController
@RequestMapping("/api")
public class ProductImageController {

	private final ProductImageService productImageService;

	public ProductImageController(ProductImageService productImageService) {
		this.productImageService = productImageService;
	}

// ------------------------------------------------------------
// 1) ADD IMAGE TO PRODUCT
// POST /api/products/{productId}/images
// ------------------------------------------------------------
	@PostMapping("/products/{productId}/images")
	public ResponseEntity<ApiResponse<ProductImage>> addImageToProduct(@PathVariable Long productId,
			@RequestBody ProductImageRequest request) {

		ProductImage saved = productImageService.addImageToProduct(productId, request);

		ApiResponse<ProductImage> resp = new ApiResponse<>(true, "Product image added", saved);

		return ResponseEntity.status(HttpStatus.CREATED).body(resp);
	}

// ------------------------------------------------------------
// 2) GET IMAGES FOR PRODUCT
// GET /api/products/{productId}/images
// ------------------------------------------------------------
	@GetMapping("/products/{productId}/images")
	public ResponseEntity<ApiResponse<List<ProductImage>>> getImagesByProduct(@PathVariable Long productId) {

		List<ProductImage> images = productImageService.getImagesByProduct(productId);
		ApiResponse<List<ProductImage>> resp = new ApiResponse<>(true, "Images fetched", images);

		return ResponseEntity.ok(resp);
	}

// ------------------------------------------------------------
// 3) UPDATE IMAGE
// PUT /api/product-images/{imageId}
// ------------------------------------------------------------
	@PutMapping("/product-images/{imageId}")
	public ResponseEntity<ApiResponse<ProductImage>> updateImage(@PathVariable Long imageId,
			@RequestBody ProductImageRequest request) {

		ProductImage updated = productImageService.updateImage(imageId, request);
		ApiResponse<ProductImage> resp = new ApiResponse<>(true, "Image updated", updated);

		return ResponseEntity.ok(resp);
	}

// ------------------------------------------------------------
// 4) SOFT DELETE IMAGE
// DELETE /api/product-images/{imageId}
// ------------------------------------------------------------
	@DeleteMapping("/product-images/{imageId}")
	public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable Long imageId) {

		productImageService.softDeleteImage(imageId);

		ApiResponse<Void> resp = new ApiResponse<>(true, "Image soft-deleted", null);

		return ResponseEntity.ok(resp);
	}

// ------------------------------------------------------------
// 5) SET PRIMARY IMAGE
// PATCH /api/product-images/{imageId}/primary?productId=1
// ------------------------------------------------------------
	@PatchMapping("/product-images/{imageId}/primary")
	public ResponseEntity<ApiResponse<Void>> setPrimary(@PathVariable Long imageId, @RequestParam Long productId) {

		productImageService.setPrimaryImage(productId, imageId);

		ApiResponse<Void> resp = new ApiResponse<>(true, "Primary image set", null);

		return ResponseEntity.ok(resp);
	}
}

