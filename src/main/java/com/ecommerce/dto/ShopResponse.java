package com.ecommerce.dto;

import java.time.LocalDateTime;


//Response DTO for returning shop details to client.
//Does not expose sensitive internal fields like isDeleted.

public class ShopResponse {

private Long id;
private Long ownerUserId;
private String name;
private String description;
private String address;
private Boolean isApproved;
private Boolean isActive;
private LocalDateTime createdAt;

public Long getId() {
    return id;
}

public void setId(Long id) {
    this.id = id;
}

public Long getOwnerUserId() {
    return ownerUserId;
}

public void setOwnerUserId(Long ownerUserId) {
    this.ownerUserId = ownerUserId;
}

public String getName() {
    return name;
}

public void setName(String name) {
    this.name = name;
}

public String getDescription() {
    return description;
}

public void setDescription(String description) {
    this.description = description;
}

public String getAddress() {
    return address;
}

public void setAddress(String address) {
    this.address = address;
}

public Boolean getIsApproved() {
    return isApproved;
}

public void setIsApproved(Boolean isApproved) {
    this.isApproved = isApproved;
}


public Boolean getIsActive() {
	return isActive;
}

public void setIsActive(Boolean isActive) {
	this.isActive = isActive;
}

public LocalDateTime getCreatedAt() {
    return createdAt;
}

public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
}

@Override
public String toString() {
	return "ShopResponse [id=" + id + ", ownerUserId=" + ownerUserId + ", name=" + name + ", description="
			+ description + ", address=" + address + ", isApproved=" + isApproved + ", isActive=" + isActive
			+ ", createdAt=" + createdAt + "]";
}

}