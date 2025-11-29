package com.ecommerce.repository.impl;

import com.ecommerce.model.Category;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.rowmapper.CategoryRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

	private final NamedParameterJdbcTemplate jdbc;

	public CategoryRepositoryImpl(NamedParameterJdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public Category save(Category c) {
		String sql = "INSERT INTO categories (parent_category_id, name, slug, is_active) "
				+ "VALUES (:parent, :name, :slug, TRUE)";
		Map<String, Object> params = new HashMap<>();
		params.put("parent", c.getParentCategoryId());
		params.put("name", c.getName());
		params.put("slug", c.getSlug());

		jdbc.update(sql, params);
		return c;
	}

	@Override
	public boolean existsById(Long id) {
		String sql = "SELECT COUNT(*) FROM categories WHERE id = :id";
		return jdbc.queryForObject(sql, Collections.singletonMap("id", id), Integer.class) > 0;
	}

	@Override
	public Optional<Category> findById(Long id) {
		String sql = "SELECT * FROM categories WHERE id = :id";
		List<Category> list = jdbc.query(sql, Collections.singletonMap("id", id), new CategoryRowMapper());
		return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
	}

	@Override
	public List<Category> findAllActive() {
		String sql = "SELECT * FROM categories WHERE is_active = TRUE ORDER BY name";
		return jdbc.query(sql, new CategoryRowMapper());
	}

	@Override
	public List<Category> findByParentCategoryId(Long parentId) {
		String sql = "SELECT * FROM categories WHERE parent_category_id = :parent AND is_active = TRUE";
		return jdbc.query(sql, Collections.singletonMap("parent", parentId), new CategoryRowMapper());
	}

	@Override
	public boolean updateActiveFlag(Long id, boolean active) {
		String sql = "UPDATE categories SET is_active = :active WHERE id = :id";
		Map<String, Object> params = new HashMap<>();
		params.put("active", active);
		params.put("id", id);
		return jdbc.update(sql, params) > 0;
	}

	@Override
	public boolean update(Category c) {
		String sql = "UPDATE categories SET name = :name, slug = :slug WHERE id = :id";
		Map<String, Object> map = new HashMap<>();
		map.put("name", c.getName());
		map.put("slug", c.getSlug());
		map.put("id", c.getId());
		return jdbc.update(sql, map) > 0;
	}
}