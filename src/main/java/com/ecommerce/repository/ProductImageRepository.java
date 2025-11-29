package com.ecommerce.repository;

import com.ecommerce.model.ProductImage;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for product_images.
 */
public interface ProductImageRepository {

	Long save(ProductImage image); // create new image

	boolean update(ProductImage image); // update path/order/primary

	boolean softDelete(Long id); // is_deleted = true

	Optional<ProductImage> findById(Long id);

	List<ProductImage> findByProductId(Long productId); // only non-deleted

	List<ProductImage> findAllByProductIdIncludeDeleted(Long productId);

	boolean clearPrimaryForProduct(Long productId); // set all is_primary = false

	boolean setPrimaryImage(Long productId, Long imageId); // set only one primary
}