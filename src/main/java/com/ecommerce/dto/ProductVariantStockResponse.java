package com.ecommerce.dto;

import java.math.BigDecimal;

public class ProductVariantStockResponse {

private Long id;
private Long productId;
private Long variantValueId;
private Integer quantity;
private BigDecimal priceOffset;

public Long getId() { return id; }
public void setId(Long id) { this.id = id; }

public Long getProductId() { return productId; }
public void setProductId(Long productId) { this.productId = productId; }

public Long getVariantValueId() { return variantValueId; }
public void setVariantValueId(Long variantValueId) { this.variantValueId = variantValueId; }

public Integer getQuantity() { return quantity; }
public void setQuantity(Integer quantity) { this.quantity = quantity; }

public BigDecimal getPriceOffset() { return priceOffset; }
public void setPriceOffset(BigDecimal priceOffset) { this.priceOffset = priceOffset; }
}