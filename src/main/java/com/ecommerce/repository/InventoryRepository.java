package com.ecommerce.repository;

import com.ecommerce.model.Inventory;

import java.util.Optional;

public interface InventoryRepository {

	Optional<Inventory> findByProductId(Long productId);

// Create initial row for a product
	boolean createInventory(Long productId, int initialQuantity);

// Update full object (generally not used often)
	boolean update(Inventory inventory);

// Increase stock (e.g. when new stock arrives)
	boolean increaseStock(Long productId, int quantity);

// Decrease stock (admin/manual)
	boolean decreaseStock(Long productId, int quantity);

	/**
	 * Reserve stock for checkout. reserved = reserved + quantity (if available >=
	 * quantity) Prevents overselling using SQL condition.
	 */
	boolean reserveStock(Long productId, int quantity);

	/**
	 * Release reserved stock when checkout is cancelled/expired.
	 */
	boolean releaseReservedStock(Long productId, int quantity);

	/**
	 * When order is confirmed/paid: - Decrease main quantity - Decrease reserved
	 */
	boolean consumeReservedOnOrder(Long productId, int quantity);
}