package com.ecommerce.repository;

import com.ecommerce.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {

	Long save(Product product); // returns generated id

	boolean update(Product product);

// Soft delete: set is_active = false
	boolean softDelete(Long id);

	Optional<Product> findById(Long id);

// Only active products (used for public listing)
	List<Product> findAllActive();

// All products for shop (including inactive) - shop owner view
	List<Product> findByShopId(Long shopId);

// Admin: all products
	List<Product> findAll();

	boolean existsBySku(String sku);

// search with simple filters (category optional)
	List<Product> search(String q, Long categoryId, int limit, int offset);
}