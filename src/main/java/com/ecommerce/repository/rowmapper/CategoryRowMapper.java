package com.ecommerce.repository.rowmapper;

import com.ecommerce.model.Category;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryRowMapper implements RowMapper<Category> {

	@Override
	public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
		Category c = new Category();

		c.setId(rs.getLong("id"));

		long parent = rs.getLong("parent_category_id");
		if (rs.wasNull()) {
			c.setParentCategoryId(null);
		} else {
			c.setParentCategoryId(parent);
		}

		c.setName(rs.getString("name"));
		c.setSlug(rs.getString("slug"));

// read is_active as int (TINYINT(1)) to avoid boolean/driver issues
		int activeInt = 0;
		try {
			activeInt = rs.getInt("is_active");
			if (rs.wasNull())
				activeInt = 0;
		} catch (SQLException ex) {
			activeInt = 0;
		}
		c.setIsActive(activeInt != 0);

		return c;
	}
}