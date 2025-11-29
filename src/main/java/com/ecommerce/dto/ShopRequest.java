package com.ecommerce.dto;

//DTO used when a shopkeeper creates or updates their shop.
//Matches allowed fields from client forms.

public class ShopRequest {

private Long ownerUserId;
private String name;
private String description;
private String address;

public ShopRequest() {
	super();
}



public ShopRequest(Long ownerUserId, String name, String description, String address) {
	super();
	this.ownerUserId = ownerUserId;
	this.name = name;
	this.description = description;
	this.address = address;
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



@Override
public String toString() {
	return "ShopRequest [ownerUserId=" + ownerUserId + ", name=" + name + ", description=" + description
			+ ", address=" + address + "]";
}


}