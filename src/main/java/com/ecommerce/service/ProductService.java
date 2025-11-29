package com.ecommerce.service;

import com.ecommerce.dto.ProductRequest;
import com.ecommerce.dto.ProductResponse;

import java.util.List;
import java.util.Optional;

public interface ProductService {

	ProductResponse createProduct(ProductRequest request);

	ProductResponse updateProduct(Long id, ProductRequest request);

	Optional<ProductResponse> getProductById(Long id);

	List<ProductResponse> getAllActiveProducts();

	List<ProductResponse> getProductsByShop(Long shopId);

	List<ProductResponse> getAllProducts(); // admin

	boolean softDeleteProduct(Long id);

	List<ProductResponse> searchProducts(String q, Long categoryId, int page, int size);
}