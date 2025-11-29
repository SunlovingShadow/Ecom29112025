package com.ecommerce.repository.impl;

import com.ecommerce.model.ProductImage;
import com.ecommerce.repository.ProductImageRepository;
import com.ecommerce.repository.rowmapper.ProductImageRowMapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductImageRepositoryImpl implements ProductImageRepository {

	private final JdbcTemplate jdbcTemplate;

	public ProductImageRepositoryImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

// ------------------------------------------------------------
// SAVE NEW IMAGE
// ------------------------------------------------------------
	@Override
	public Long save(ProductImage image) {
		String sql = """
				INSERT INTO product_images
				(product_id, image_path, is_primary, sort_image_order, is_deleted)
				VALUES (?, ?, ?, ?, ?)
				""";

		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setLong(1, image.getProductId());
			ps.setString(2, image.getImagePath());
			ps.setBoolean(3, image.isPrimary());
			ps.setInt(4, image.getSortImageOrder());
			ps.setBoolean(5, image.isDeleted());
			return ps;
		}, keyHolder);

		return keyHolder.getKey().longValue();
	}

// ------------------------------------------------------------
// UPDATE IMAGE
// ------------------------------------------------------------
	@Override
	public boolean update(ProductImage image) {
		String sql = """
				UPDATE product_images SET
				image_path = ?,
				is_primary = ?,
				sort_image_order = ?,
				is_deleted = ?
				WHERE id = ?
				""";

		int rows = jdbcTemplate.update(sql, image.getImagePath(), image.isPrimary(), image.getSortImageOrder(),
				image.isDeleted(), image.getId());

		return rows > 0;
	}

// ------------------------------------------------------------
// SOFT DELETE IMAGE (is_deleted = true)
// ------------------------------------------------------------
	@Override
	public boolean softDelete(Long id) {
		String sql = "UPDATE product_images SET is_deleted = TRUE WHERE id = ?";
		return jdbcTemplate.update(sql, id) > 0;
	}

// ------------------------------------------------------------
// FIND BY ID
// ------------------------------------------------------------
	@Override
	public Optional<ProductImage> findById(Long id) {
		String sql = "SELECT * FROM product_images WHERE id = ?";

		List<ProductImage> list = jdbcTemplate.query(sql, new ProductImageRowMapper(), id);

		return list.stream().findFirst();
	}

// ------------------------------------------------------------
// FIND NON-DELETED IMAGES BY PRODUCT
// ------------------------------------------------------------
	@Override
	public List<ProductImage> findByProductId(Long productId) {
		String sql = """
				SELECT * FROM product_images
				WHERE product_id = ?
				AND is_deleted = FALSE
				ORDER BY sort_image_order ASC, id ASC
				""";

		return jdbcTemplate.query(sql, new ProductImageRowMapper(), productId);
	}

// ------------------------------------------------------------
// FIND ALL IMAGES FOR PRODUCT (including deleted)
// ------------------------------------------------------------
	@Override
	public List<ProductImage> findAllByProductIdIncludeDeleted(Long productId) {
		String sql = """
				SELECT * FROM product_images
				WHERE product_id = ?
				ORDER BY sort_image_order ASC, id ASC
				""";

		return jdbcTemplate.query(sql, new ProductImageRowMapper(), productId);
	}

// ------------------------------------------------------------
// CLEAR PRIMARY FLAG FOR ALL IMAGES OF PRODUCT
// ------------------------------------------------------------
	@Override
	public boolean clearPrimaryForProduct(Long productId) {
		String sql = """
				UPDATE product_images
				SET is_primary = FALSE
				WHERE product_id = ?
				""";
		return jdbcTemplate.update(sql, productId) > 0;
	}

// ------------------------------------------------------------
// SET PRIMARY IMAGE (ENSURE ONLY ONE PRIMARY)
// ------------------------------------------------------------
	@Override
	public boolean setPrimaryImage(Long productId, Long imageId) {
// Step 1: clear all primary flags
		clearPrimaryForProduct(productId);

// Step 2: set primary for specific image
		String sql = """
				UPDATE product_images
				SET is_primary = TRUE
				WHERE id = ? AND product_id = ? AND is_deleted = FALSE
				""";

		return jdbcTemplate.update(sql, imageId, productId) > 0;
	}
}