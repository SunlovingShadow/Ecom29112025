package com.ecommerce.dto;

public class CategoryResponse {

	private Long id;
	private Long parentCategoryId;
	private String name;
	private String slug;
	private Boolean isActive;

	public CategoryResponse() {
	}

	public CategoryResponse(Long id, Long parentCategoryId, String name, String slug, Boolean isActive) {
		this.id = id;
		this.parentCategoryId = parentCategoryId;
		this.name = name;
		this.slug = slug;
		this.isActive = isActive;
	}

// getters & setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
}