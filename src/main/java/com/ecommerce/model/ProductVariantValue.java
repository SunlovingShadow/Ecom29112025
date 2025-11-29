package com.ecommerce.model;

public class ProductVariantValue {

private Long id;
private Long groupId; // FK -> product_variant_group.id
private String valueName; // S, M, L / Red, Blue

// getters & setters
public Long getId() { return id; }
public void setId(Long id) { this.id = id; }

public Long getGroupId() { return groupId; }
public void setGroupId(Long groupId) { this.groupId = groupId; }

public String getValueName() { return valueName; }
public void setValueName(String valueName) { this.valueName = valueName; }
}