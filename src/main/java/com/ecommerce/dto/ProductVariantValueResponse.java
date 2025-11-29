package com.ecommerce.dto;

public class ProductVariantValueResponse {
private Long id;
private Long groupId;
private String valueName;

public Long getId() { return id; }
public void setId(Long id) { this.id = id; }

public Long getGroupId() { return groupId; }
public void setGroupId(Long groupId) { this.groupId = groupId; }

public String getValueName() { return valueName; }
public void setValueName(String valueName) { this.valueName = valueName; }
}