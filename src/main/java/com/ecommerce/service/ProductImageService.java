package com.ecommerce.service;

import com.ecommerce.dto.ProductImageRequest;
import com.ecommerce.model.ProductImage;

import java.util.List;

public interface ProductImageService {

	ProductImage addImageToProduct(Long productId, ProductImageRequest request);

	List<ProductImage> getImagesByProduct(Long productId);

	ProductImage updateImage(Long imageId, ProductImageRequest request);

	void softDeleteImage(Long imageId);

	void setPrimaryImage(Long productId, Long imageId);
}