package com.ecommerce.service;

import com.ecommerce.dto.*;

import java.util.List;

public interface ProductVariantService {

// groups
ProductVariantGroupResponse createGroup(Long productId, ProductVariantGroupRequest request);
List<ProductVariantGroupResponse> getGroupsByProduct(Long productId);
ProductVariantGroupResponse updateGroup(Long groupId, ProductVariantGroupRequest request);
boolean deleteGroup(Long groupId); // hard delete

// values
ProductVariantValueResponse createValue(Long groupId, ProductVariantValueRequest request);
List<ProductVariantValueResponse> getValuesByGroup(Long groupId);
ProductVariantValueResponse updateValue(Long valueId, ProductVariantValueRequest request);
boolean deleteValue(Long valueId);

// stock
ProductVariantStockResponse upsertStock(Long productId, Long variantValueId, ProductVariantStockRequest request);
List<ProductVariantStockResponse> getStockByProduct(Long productId);
}

