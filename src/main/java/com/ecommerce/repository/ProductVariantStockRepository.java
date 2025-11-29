package com.ecommerce.repository;

import com.ecommerce.model.ProductVariantStock;

import java.util.List;
import java.util.Optional;

public interface ProductVariantStockRepository {

Long save(ProductVariantStock stock);

boolean update(ProductVariantStock stock);

Optional<ProductVariantStock> findById(Long id);

Optional<ProductVariantStock> findByProductAndValue(Long productId, Long valueId);

List<ProductVariantStock> findByProductId(Long productId);
}