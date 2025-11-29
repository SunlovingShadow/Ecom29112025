package com.ecommerce.dto;

/**
 * Request DTO for adding/updating a product image.
 */
public class ProductImageRequest {

	private String imagePath; // e.g. "/product-images/tshirtyellow.png"
	private Boolean primary; // true / false
	private Integer sortImageOrder; // 0,1,2...

// -------- Getters & Setters --------

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public Boolean getPrimary() {
		return primary;
	}

	public void setPrimary(Boolean primary) {
		this.primary = primary;
	}

	public Integer getSortImageOrder() {
		return sortImageOrder;
	}

	public void setSortImageOrder(Integer sortImageOrder) {
		this.sortImageOrder = sortImageOrder;
	}
}