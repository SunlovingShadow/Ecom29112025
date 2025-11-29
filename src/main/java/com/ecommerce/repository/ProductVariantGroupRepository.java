package com.ecommerce.repository;

import com.ecommerce.model.ProductVariantGroup;
import java.util.List;
import java.util.Optional;

public interface ProductVariantGroupRepository {

Long save(ProductVariantGroup group);

boolean update(ProductVariantGroup group);

boolean delete(Long id); // hard delete

Optional<ProductVariantGroup> findById(Long id);

List<ProductVariantGroup> findByProductId(Long productId);
}