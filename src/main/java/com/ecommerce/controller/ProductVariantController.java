package com.ecommerce.controller;

import com.ecommerce.dto.*;
import com.ecommerce.service.ProductVariantService;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/variants")
public class ProductVariantController {

private final ProductVariantService variantService;

public ProductVariantController(ProductVariantService variantService) {
this.variantService = variantService;
}

// ------- GROUPS -------

@PostMapping("/groups")
public ResponseEntity<ProductVariantGroupResponse> createGroup(
@PathVariable Long productId,
@RequestBody ProductVariantGroupRequest request) {
return new ResponseEntity<>(
variantService.createGroup(productId, request),
HttpStatus.CREATED
);
}

@GetMapping("/groups")
public ResponseEntity<List<ProductVariantGroupResponse>> getGroups(
@PathVariable Long productId) {
return ResponseEntity.ok(variantService.getGroupsByProduct(productId));
}

@PutMapping("/groups/{groupId}")
public ResponseEntity<ProductVariantGroupResponse> updateGroup(
@PathVariable Long productId,
@PathVariable Long groupId,
@RequestBody ProductVariantGroupRequest request) {
// productId unused, just for path consistency
return ResponseEntity.ok(variantService.updateGroup(groupId, request));
}

@DeleteMapping("/groups/{groupId}")
public ResponseEntity<String> deleteGroup(@PathVariable Long groupId) {
variantService.deleteGroup(groupId);
return ResponseEntity.ok("Variant group deleted");
}

// ------- VALUES -------

@PostMapping("/groups/{groupId}/values")
public ResponseEntity<ProductVariantValueResponse> createValue(
@PathVariable Long groupId,
@RequestBody ProductVariantValueRequest request) {
return new ResponseEntity<>(
variantService.createValue(groupId, request),
HttpStatus.CREATED
);
}

@GetMapping("/groups/{groupId}/values")
public ResponseEntity<List<ProductVariantValueResponse>> getValues(
@PathVariable Long groupId) {
return ResponseEntity.ok(variantService.getValuesByGroup(groupId));
}

@PutMapping("/values/{valueId}")
public ResponseEntity<ProductVariantValueResponse> updateValue(
@PathVariable Long valueId,
@RequestBody ProductVariantValueRequest request) {
return ResponseEntity.ok(variantService.updateValue(valueId, request));
}

@DeleteMapping("/values/{valueId}")
public ResponseEntity<String> deleteValue(@PathVariable Long valueId) {
variantService.deleteValue(valueId);
return ResponseEntity.ok("Variant value deleted");
}

// ------- STOCK -------

@PostMapping("/values/{valueId}/stock")
public ResponseEntity<ProductVariantStockResponse> upsertStock(
@PathVariable Long productId,
@PathVariable Long valueId,
@RequestBody ProductVariantStockRequest request) {
return ResponseEntity.ok(
variantService.upsertStock(productId, valueId, request)
);
}

@GetMapping("/stock")
public ResponseEntity<List<ProductVariantStockResponse>> getStock(
@PathVariable Long productId) {
return ResponseEntity.ok(variantService.getStockByProduct(productId));
}
}