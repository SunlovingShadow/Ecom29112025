package com.ecommerce.repository.rowmapper;

import com.ecommerce.model.ProductVariantGroup;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductVariantGroupRowMapper implements RowMapper<ProductVariantGroup> {
@Override
public ProductVariantGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
ProductVariantGroup g = new ProductVariantGroup();
g.setId(rs.getLong("id"));
g.setProductId(rs.getLong("product_id"));
g.setGroupName(rs.getString("group_name"));
return g;
}
}