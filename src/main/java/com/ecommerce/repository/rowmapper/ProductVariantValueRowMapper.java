package com.ecommerce.repository.rowmapper;

import com.ecommerce.model.ProductVariantValue;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductVariantValueRowMapper implements RowMapper<ProductVariantValue> {
@Override
public ProductVariantValue mapRow(ResultSet rs, int rowNum) throws SQLException {
ProductVariantValue v = new ProductVariantValue();
v.setId(rs.getLong("id"));
v.setGroupId(rs.getLong("group_id"));
v.setValueName(rs.getString("value_name"));
return v;
}
}