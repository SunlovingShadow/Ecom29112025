package com.ecommerce.controller;

import com.ecommerce.dto.InventoryResponse;
import com.ecommerce.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * INVENTORY CONTROLLER For admin/shopkeeper + internal usage via other
 * services. In real setup, most of these will be called from
 * OrderService/CartService, not directly from frontend.
 */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

	private final InventoryService inventoryService;

	public InventoryController(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

// ------------------------------------------------------------
// GET INVENTORY STATUS
// ------------------------------------------------------------
	@GetMapping("/{productId}")
	public ResponseEntity<InventoryResponse> getInventory(@PathVariable Long productId) {
		InventoryResponse response = inventoryService.getInventory(productId);
		return ResponseEntity.ok(response);
	}

// ------------------------------------------------------------
// INIT / RESET INVENTORY (ADMIN/SHOPKEEPER)
// ------------------------------------------------------------
	@PostMapping("/{productId}/init")
	public ResponseEntity<InventoryResponse> initInventory(@PathVariable Long productId, @RequestParam int quantity) {

		InventoryResponse response = inventoryService.createOrInitInventory(productId, quantity);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

// ------------------------------------------------------------
// ADD STOCK
// ------------------------------------------------------------
	@PostMapping("/{productId}/add")
	public ResponseEntity<InventoryResponse> addStock(@PathVariable Long productId, @RequestParam int quantity) {

		InventoryResponse response = inventoryService.addStock(productId, quantity);
		return ResponseEntity.ok(response);
	}

// ------------------------------------------------------------
// DECREASE STOCK
// ------------------------------------------------------------
	@PostMapping("/{productId}/decrease")
	public ResponseEntity<InventoryResponse> decreaseStock(@PathVariable Long productId, @RequestParam int quantity) {

		InventoryResponse response = inventoryService.decreaseStock(productId, quantity);
		return ResponseEntity.ok(response);
	}

// ------------------------------------------------------------
// RESERVE STOCK - used during checkout
// ------------------------------------------------------------
	@PostMapping("/{productId}/reserve")
	public ResponseEntity<InventoryResponse> reserveStock(@PathVariable Long productId, @RequestParam int quantity) {

		InventoryResponse response = inventoryService.reserveStock(productId, quantity);
		return ResponseEntity.ok(response);
	}

// ------------------------------------------------------------
// RELEASE RESERVED STOCK
// ------------------------------------------------------------
	@PostMapping("/{productId}/release")
	public ResponseEntity<InventoryResponse> releaseStock(@PathVariable Long productId, @RequestParam int quantity) {

		InventoryResponse response = inventoryService.releaseReserved(productId, quantity);
		return ResponseEntity.ok(response);
	}

// ------------------------------------------------------------
// CONSUME RESERVED ON ORDER CONFIRM
// ------------------------------------------------------------
	@PostMapping("/{productId}/consume")
	public ResponseEntity<InventoryResponse> consumeReserved(@PathVariable Long productId, @RequestParam int quantity) {

		InventoryResponse response = inventoryService.consumeReservedOnOrder(productId, quantity);
		return ResponseEntity.ok(response);
	}
}