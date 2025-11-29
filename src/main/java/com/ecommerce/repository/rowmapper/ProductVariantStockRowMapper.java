package com.ecommerce.repository.rowmapper;

import com.ecommerce.model.ProductVariantStock;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;

public class ProductVariantStockRowMapper implements RowMapper<ProductVariantStock> {
@Override
public ProductVariantStock mapRow(ResultSet rs, int rowNum) throws SQLException {
ProductVariantStock s = new ProductVariantStock();
s.setId(rs.getLong("id"));
s.setProductId(rs.getLong("product_id"));
s.setVariantValueId(rs.getLong("variant_value_id"));
s.setQuantity(rs.getInt("quantity"));
s.setPriceOffset(rs.getBigDecimal("price_offset"));
return s;
}
}