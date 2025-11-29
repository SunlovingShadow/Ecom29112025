package com.ecommerce.dto;

import java.time.LocalDateTime;

public class AdminLogResponse {
	
	private Long id;
	private Long adminUserId;
	private String action;
	private LocalDateTime createdAt;
	
	public AdminLogResponse() {
		super();
	}

	public AdminLogResponse(Long id, Long adminUserId, String action, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.adminUserId = adminUserId;
		this.action = action;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAdminUserId() {
		return adminUserId;
	}

	public void setAdminUserId(Long adminUserId) {
		this.adminUserId = adminUserId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "AdminLogResponse [id=" + id + ", adminUserId=" + adminUserId + ", action=" + action + ", createdAt="
				+ createdAt + "]";
	}
}