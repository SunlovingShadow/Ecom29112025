package com.ecommerce.dto;

public class ProductVariantGroupResponse {
private Long id;
private Long productId;
private String groupName;

public Long getId() { return id; }
public void setId(Long id) { this.id = id; }

public Long getProductId() { return productId; }
public void setProductId(Long productId) { this.productId = productId; }

public String getGroupName() { return groupName; }
public void setGroupName(String groupName) { this.groupName = groupName; }
}