package com.ecommerce.dto;

public class ReviewRequest {
	
	private Long productId;
	private int rating;
	private String title;
	private String body;
	
	public ReviewRequest() {
		super();
	}

	public ReviewRequest(Long productId, int rating, String title, String body) {
		super();
		this.productId = productId;
		this.rating = rating;
		this.title = title;
		this.body = body;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
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

	@Override
	public String toString() {
		return "ReviewRequest [productId=" + productId + ", rating=" + rating + ", title=" + title + ", body=" + body
				+ "]";
	}
	
}