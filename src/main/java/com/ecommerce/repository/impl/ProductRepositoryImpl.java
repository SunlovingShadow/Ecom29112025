package com.ecommerce.repository.impl;

import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.rowmapper.ProductRowMapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

	private final JdbcTemplate jdbcTemplate;

	public ProductRepositoryImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public Long save(Product product) {
		String sql = """
				INSERT INTO products
				(shop_id, category_id, sku, name, short_description, description, selling_price, mrp, is_active)
				VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
				""";

		KeyHolder kh = new GeneratedKeyHolder();

		jdbcTemplate.update(conn -> {
			PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setLong(1, product.getShopId());
			if (product.getCategoryId() != null)
				ps.setLong(2, product.getCategoryId());
			else
				ps.setNull(2, java.sql.Types.BIGINT);
			ps.setString(3, product.getSku());
			ps.setString(4, product.getName());
			ps.setString(5, product.getShortDescription());
			ps.setString(6, product.getDescription());
			ps.setDouble(7, product.getSellingPrice());
			if (product.getMrp() != null)
				ps.setDouble(8, product.getMrp());
			else
				ps.setNull(8, java.sql.Types.DECIMAL);
			ps.setBoolean(9, product.getIsActive() == null ? true : product.getIsActive());
			return ps;
		}, kh);

		Number key = kh.getKey();
		return (key != null) ? key.longValue() : null;
	}

	@Override
	public boolean update(Product product) {
		String sql = """
				UPDATE products SET
				shop_id = ?, category_id = ?, sku = ?, name = ?,
				short_description = ?, description = ?, selling_price = ?, mrp = ?, is_active = ?
				WHERE id = ?
				""";

		int updated = jdbcTemplate.update(sql, product.getShopId(), product.getCategoryId(), product.getSku(),
				product.getName(), product.getShortDescription(), product.getDescription(), product.getSellingPrice(),
				product.getMrp(), product.getIsActive(), product.getId());
		return updated > 0;
	}

	@Override
	public boolean softDelete(Long id) {
		String sql = "UPDATE products SET is_active = FALSE WHERE id = ?";
		return jdbcTemplate.update(sql, id) > 0;
	}

	@Override
	public Optional<Product> findById(Long id) {
		String sql = "SELECT * FROM products WHERE id = ?";
		List<Product> list = jdbcTemplate.query(sql, new ProductRowMapper(), id);
		return list.stream().findFirst();
	}

	@Override
	public List<Product> findAllActive() {
		String sql = "SELECT * FROM products WHERE is_active = TRUE ORDER BY created_at DESC";
		return jdbcTemplate.query(sql, new ProductRowMapper());
	}

	@Override
	public List<Product> findByShopId(Long shopId) {
		String sql = "SELECT * FROM products WHERE shop_id = ? ORDER BY created_at DESC";
		return jdbcTemplate.query(sql, new ProductRowMapper(), shopId);
	}

	@Override
	public List<Product> findAll() {
		String sql = "SELECT * FROM products ORDER BY created_at DESC";
		return jdbcTemplate.query(sql, new ProductRowMapper());
	}

	@Override
	public boolean existsBySku(String sku) {
		String sql = "SELECT COUNT(*) FROM products WHERE sku = ?";
		Integer count = jdbcTemplate.queryForObject(sql, Integer.class, sku);
		return count != null && count > 0;
	}

	@Override
	public List<Product> search(String q, Long categoryId, int limit, int offset) {
		String base = "SELECT * FROM products WHERE is_active = TRUE";
		StringBuilder sb = new StringBuilder(base);
		Object[] params;
		if (q != null && !q.isBlank()) {
			sb.append(" AND (name LIKE ? OR short_description LIKE ? OR description LIKE ? OR sku LIKE ?)");
			if (categoryId != null) {
				sb.append(" AND category_id = ?");
				sb.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");
				params = new Object[] { "%" + q + "%", "%" + q + "%", "%" + q + "%", "%" + q + "%", categoryId, limit,
						offset };
			} else {
				sb.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");
				params = new Object[] { "%" + q + "%", "%" + q + "%", "%" + q + "%", "%" + q + "%", limit, offset };
			}
		} else {
			if (categoryId != null) {
				sb.append(" AND category_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?");
				params = new Object[] { categoryId, limit, offset };
			} else {
				sb.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");
				params = new Object[] { limit, offset };
			}
		}
		return jdbcTemplate.query(sb.toString(), new ProductRowMapper(), params);
	}
}