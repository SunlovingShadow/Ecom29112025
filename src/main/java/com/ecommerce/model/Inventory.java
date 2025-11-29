package com.ecommerce.model;

/**
 * Inventory entity for table 'inventory' Tracks: - quantity = total stock -
 * reserved = stock blocked during checkout Available = quantity - reserved
 */
public class Inventory {

	private Long productId; // PK and FK -> products.id
	private Integer quantity;
	private Integer reserved;

// -------- Getters & Setters --------

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getReserved() {
		return reserved;
	}

	public void setReserved(Integer reserved) {
		this.reserved = reserved;
	}

// Helper: available stock
	public int getAvailable() {
		if (quantity == null || reserved == null)
			return 0;
		return quantity - reserved;
	}
}