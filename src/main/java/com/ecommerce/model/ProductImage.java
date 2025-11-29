package com.ecommerce.model;

/**
 * Represents a single image for a product.
 *
 * Features: - Multiple images per product (same productId) - Primary image
 * support (isPrimary) - Display order support (sortImageOrder) - Soft delete
 * via isDeleted flag
 */
public class ProductImage {

	private Long id; // PK
	private Long productId; // FK -> products.id

	private String imagePath; // URL/path to image file
	private boolean isPrimary; // true => main thumbnail
	private int sortImageOrder; // for gallery ordering, 0 = first
	private boolean isDeleted; // soft delete flag

// --------- Getters & Setters ---------

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public void setPrimary(boolean primary) {
		isPrimary = primary;
	}

	public int getSortImageOrder() {
		return sortImageOrder;
	}

	public void setSortImageOrder(int sortImageOrder) {
		this.sortImageOrder = sortImageOrder;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean deleted) {
		isDeleted = deleted;
	}
}