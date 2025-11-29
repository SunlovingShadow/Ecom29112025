package com.ecommerce.dto;

import java.math.BigDecimal;

public class ProductVariantStockRequest {

private Integer quantity;
private BigDecimal priceOffset; // can be null = 0

public Integer getQuantity() { return quantity; }
public void setQuantity(Integer quantity) { this.quantity = quantity; }

public BigDecimal getPriceOffset() { return priceOffset; }
public void setPriceOffset(BigDecimal priceOffset) { this.priceOffset = priceOffset; }
}