package com.ecommerce.service.impl;

import com.ecommerce.dto.ProductRequest;
import com.ecommerce.dto.ProductResponse;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;

	public ProductServiceImpl(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Override
	public ProductResponse createProduct(ProductRequest request) {
// validations
		if (request.getSku() == null || request.getSku().isBlank()) {
			throw new BadRequestException("SKU is required");
		}
		if (productRepository.existsBySku(request.getSku())) {
			throw new BadRequestException("SKU already exists");
		}
		if (request.getSellingPrice() == null) {
			throw new BadRequestException("Selling price required");
		}

		Product p = new Product();
		p.setShopId(request.getShopId());
		p.setCategoryId(request.getCategoryId());
		p.setSku(request.getSku());
		p.setName(request.getName());
		p.setShortDescription(request.getShortDescription());
		p.setDescription(request.getDescription());
		p.setSellingPrice(request.getSellingPrice());
		p.setMrp(request.getMrp());
		p.setIsActive(request.getIsActive() == null ? true : request.getIsActive());

		Long id = productRepository.save(p);
		p.setId(id);
		return mapToResponse(p);
	}

	@Override
	public ProductResponse updateProduct(Long id, ProductRequest request) {
		Product existing = productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found"));

// If SKU updated, check uniqueness
		if (request.getSku() != null && !request.getSku().equals(existing.getSku())) {
			if (productRepository.existsBySku(request.getSku())) {
				throw new BadRequestException("SKU already exists");
			}
			existing.setSku(request.getSku());
		}

		if (request.getName() != null)
			existing.setName(request.getName());
		if (request.getShortDescription() != null)
			existing.setShortDescription(request.getShortDescription());
		if (request.getDescription() != null)
			existing.setDescription(request.getDescription());
		if (request.getSellingPrice() != null)
			existing.setSellingPrice(request.getSellingPrice());
		if (request.getMrp() != null)
			existing.setMrp(request.getMrp());
		if (request.getCategoryId() != null)
			existing.setCategoryId(request.getCategoryId());
		if (request.getShopId() != null)
			existing.setShopId(request.getShopId());
		if (request.getIsActive() != null)
			existing.setIsActive(request.getIsActive());

		productRepository.update(existing);
		return mapToResponse(existing);
	}

	@Override
	public Optional<ProductResponse> getProductById(Long id) {
		return productRepository.findById(id).map(this::mapToResponse);
	}

	@Override
	public List<ProductResponse> getAllActiveProducts() {
		return productRepository.findAllActive().stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	@Override
	public List<ProductResponse> getProductsByShop(Long shopId) {
		return productRepository.findByShopId(shopId).stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	@Override
	public List<ProductResponse> getAllProducts() {
		return productRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	@Override
	public boolean softDeleteProduct(Long id) {
// do not hard delete - mark inactive
		if (productRepository.findById(id).isEmpty()) {
			throw new ResourceNotFoundException("Product not found");
		}
		return productRepository.softDelete(id);
	}

	@Override
	public List<ProductResponse> searchProducts(String q, Long categoryId, int page, int size) {
		int limit = size <= 0 ? 20 : size;
		int offset = Math.max(0, page) * limit;
		return productRepository.search(q, categoryId, limit, offset).stream().map(this::mapToResponse)
				.collect(Collectors.toList());
	}

// mapper
	private ProductResponse mapToResponse(Product p) {
		ProductResponse r = new ProductResponse();
		r.setId(p.getId());
		r.setShopId(p.getShopId());
		r.setCategoryId(p.getCategoryId());
		r.setSku(p.getSku());
		r.setName(p.getName());
		r.setShortDescription(p.getShortDescription());
		r.setDescription(p.getDescription());
		r.setSellingPrice(p.getSellingPrice());
		r.setMrp(p.getMrp());
		r.setIsActive(p.getIsActive());
		r.setCreatedAt(p.getCreatedAt());
		r.setUpdatedAt(p.getUpdatedAt());
		return r;
	}
}