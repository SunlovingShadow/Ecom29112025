package com.ecommerce.model;

import java.time.LocalDateTime;


//SHOP ENTITY
//Represents a shop owned by a Shopkeeper user.
//SQL table 'shops'.

public class Shop {

private Long id;
private Long ownerUserId;     // references users(id)
private String name;
private String description;
private String address;
private Boolean isApproved;   // admin approval 
private Boolean isActive;    // soft delete column
private LocalDateTime createdAt;

public Shop() {
	super();
}

public Shop(Long id, Long ownerUserId, String name, String description, String address, Boolean isApproved,
		Boolean isActive, LocalDateTime createdAt) {
	super();
	this.id = id;
	this.ownerUserId = ownerUserId;
	this.name = name;
	this.description = description;
	this.address = address;
	this.isApproved = isApproved;
	this.isActive = isActive;
	this.createdAt = createdAt;
}

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
	return "Shop [id=" + id + ", ownerUserId=" + ownerUserId + ", name=" + name + ", description=" + description
			+ ", address=" + address + ", isApproved=" + isApproved + ", isActive=" + isActive + ", createdAt="
			+ createdAt + "]";
}


}