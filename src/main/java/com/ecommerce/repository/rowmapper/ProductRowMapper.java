package com.ecommerce.repository.rowmapper;

import com.ecommerce.model.Product;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ProductRowMapper implements RowMapper<Product> {

	@Override
	public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
		Product p = new Product();
		p.setId(rs.getLong("id"));
		p.setShopId(rs.getLong("shop_id"));
		long catId = rs.getLong("category_id");
		if (!rs.wasNull())
			p.setCategoryId(catId);

		p.setSku(rs.getString("sku"));
		p.setName(rs.getString("name"));
		p.setShortDescription(rs.getString("short_description"));
		p.setDescription(rs.getString("description"));
		p.setSellingPrice(rs.getDouble("selling_price"));
		p.setMrp(rs.getDouble("mrp"));
		p.setIsActive(rs.getBoolean("is_active"));

		if (rs.getTimestamp("created_at") != null)
			p.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
		if (rs.getTimestamp("updated_at") != null)
			p.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

		return p;
	}
}