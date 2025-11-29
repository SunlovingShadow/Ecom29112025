package com.ecommerce.dto;

/**
* DTO representing inventory status for a product.
*/
public class InventoryResponse {

private Long productId;
private Integer quantity;
private Integer reserved;
private Integer available; // quantity - reserved

// -------- Getters & Setters --------

public Long getProductId() {
return productId;
}

public void setProductId(Long productId) {
this.productId = productId;
}

public Integer getQuantity() {
return quantity;
}

public void setQuantity(Integer quantity) {
this.quantity = quantity;
}

public Integer getReserved() {
return reserved;
}

public void setReserved(Integer reserved) {
this.reserved = reserved;
}

public Integer getAvailable() {
return available;
}

public void setAvailable(Integer available) {
this.available = available;
}
}