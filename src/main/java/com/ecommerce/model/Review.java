package com.ecommerce.model;

import java.sql.Timestamp;

public class Review {
	
	private Long id;
	private Long productId;
	private Long userId;
	private int rating;
	private String title;
	private String body;
	private boolean isDeleted;
	private Timestamp createdAt;
	
	public Review() {
		super();
	}

	public Review(Long id, Long productId, Long userId, int rating, String title, String body, boolean isDeleted,
			Timestamp createdAt) {
		super();
		this.id = id;
		this.productId = productId;
		this.userId = userId;
		this.rating = rating;
		this.title = title;
		this.body = body;
		this.isDeleted = isDeleted;
		this.createdAt = createdAt;
	}

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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "Review [id=" + id + ", productId=" + productId + ", userId=" + userId + ", rating=" + rating
				+ ", title=" + title + ", body=" + body + ", isDeleted=" + isDeleted + ", createdAt=" + createdAt + "]";
	}

}