package com.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryRequest {

	private Long parentCategoryId;

	@NotBlank(message = "Category name is required")
	@Size(max = 150, message = "Category name must be <= 150 characters")
	private String name;

	@NotBlank(message = "Slug is required")
	@Size(max = 150, message = "Slug must be <= 150 characters")
	private String slug;

	public CategoryRequest() {
	}

// getters & setters
	public Long getParentCategoryId() {
		return parentCategoryId;
	}

	public void setParentCategoryId(Long parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}
}