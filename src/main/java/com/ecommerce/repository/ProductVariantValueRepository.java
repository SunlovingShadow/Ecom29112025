package com.ecommerce.repository;

import com.ecommerce.model.ProductVariantValue;

import java.util.List;
import java.util.Optional;

public interface ProductVariantValueRepository {

Long save(ProductVariantValue value);

boolean update(ProductVariantValue value);

boolean delete(Long id);

Optional<ProductVariantValue> findById(Long id);

List<ProductVariantValue> findByGroupId(Long groupId);
}