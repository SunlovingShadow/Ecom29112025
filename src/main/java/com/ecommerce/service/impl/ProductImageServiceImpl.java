package com.ecommerce.service.impl;

import com.ecommerce.dto.ProductImageRequest;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.ProductImage;
import com.ecommerce.repository.ProductImageRepository;
import com.ecommerce.service.ProductImageService;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductImageServiceImpl implements ProductImageService {

	private final ProductImageRepository productImageRepository;

	public ProductImageServiceImpl(ProductImageRepository productImageRepository) {
		this.productImageRepository = productImageRepository;
	}

// ------------------------------------------------------------
// ADD IMAGE TO PRODUCT
// ------------------------------------------------------------
	@Override
	public ProductImage addImageToProduct(Long productId, ProductImageRequest request) {
		
		//till the time image is stored in static folder, this checks whether image exists or not
		String imagePath = request.getImagePath();
		if(imagePath == null || imagePath.isBlank()) {
			throw new BadRequestException("Image path is required.");
		}
		
		String classpathLocation="static" + (imagePath.startsWith("/") ? imagePath: "/" + imagePath);
		
		ClassPathResource resource = new ClassPathResource(classpathLocation);
		if(!resource.exists()) {
			throw new BadRequestException("Image file not found in static folder: " + imagePath);
		}

		//now sure that image exists
		ProductImage image = new ProductImage();
		image.setProductId(productId);
		image.setImagePath(request.getImagePath());

// default false if null
		boolean primary = request.getPrimary() != null && request.getPrimary();
		image.setPrimary(primary);

// default 0 if null
		int sortOrder = request.getSortImageOrder() != null ? request.getSortImageOrder() : 0;
		image.setSortImageOrder(sortOrder);

		image.setDeleted(false);

		Long id = productImageRepository.save(image);
		image.setId(id);

		return image;
	}

// ------------------------------------------------------------
// GET IMAGES FOR PRODUCT (NON-DELETED)
// ------------------------------------------------------------
	@Override
	public List<ProductImage> getImagesByProduct(Long productId) {
		return productImageRepository.findByProductId(productId);
	}

// ------------------------------------------------------------
// UPDATE IMAGE
// ------------------------------------------------------------
	@Override
	public ProductImage updateImage(Long imageId, ProductImageRequest request) {
		ProductImage existing = productImageRepository.findById(imageId)
				.orElseThrow(() -> new ResourceNotFoundException("Product image not found"));

		if (request.getImagePath() != null) {
			existing.setImagePath(request.getImagePath());
		}

		if (request.getPrimary() != null) {
			existing.setPrimary(request.getPrimary());
		}

		if (request.getSortImageOrder() != null) {
			existing.setSortImageOrder(request.getSortImageOrder());
		}

		productImageRepository.update(existing);
		return existing;
	}

// ------------------------------------------------------------
// SOFT DELETE
// ------------------------------------------------------------
	@Override
	public void softDeleteImage(Long imageId) {
		ProductImage existing = productImageRepository.findById(imageId)
				.orElseThrow(() -> new ResourceNotFoundException("Product image not found"));

		productImageRepository.softDelete(existing.getId());
	}

// ------------------------------------------------------------
// SET PRIMARY IMAGE FOR PRODUCT
// ------------------------------------------------------------
	@Override
	public void setPrimaryImage(Long productId, Long imageId) {
// check image exists first
		ProductImage existing = productImageRepository.findById(imageId)
				.orElseThrow(() -> new ResourceNotFoundException("Product image not found"));

		if (!existing.getProductId().equals(productId)) {
			throw new ResourceNotFoundException("Image does not belong to this product");
		}

		productImageRepository.setPrimaryImage(productId, imageId);
	}
}