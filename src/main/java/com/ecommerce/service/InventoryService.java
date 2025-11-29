package com.ecommerce.service;

import com.ecommerce.dto.InventoryResponse;

public interface InventoryService {

	InventoryResponse getInventory(Long productId);

	InventoryResponse createOrInitInventory(Long productId, int quantity);

	InventoryResponse addStock(Long productId, int quantity);

	InventoryResponse decreaseStock(Long productId, int quantity);

	InventoryResponse reserveStock(Long productId, int quantity);

	InventoryResponse releaseReserved(Long productId, int quantity);

	InventoryResponse consumeReservedOnOrder(Long productId, int quantity);
}