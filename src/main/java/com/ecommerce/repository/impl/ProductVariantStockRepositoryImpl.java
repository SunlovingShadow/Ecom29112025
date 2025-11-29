
package com.ecommerce.repository.impl;

import com.ecommerce.model.ProductVariantStock;
import com.ecommerce.repository.ProductVariantStockRepository;
import com.ecommerce.repository.rowmapper.ProductVariantStockRowMapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductVariantStockRepositoryImpl implements ProductVariantStockRepository {

private final JdbcTemplate jdbcTemplate;

public ProductVariantStockRepositoryImpl(JdbcTemplate jdbcTemplate) {
this.jdbcTemplate = jdbcTemplate;
}

@Override
public Long save(ProductVariantStock s) {
String sql = """
INSERT INTO product_variant_stock (product_id, variant_value_id, quantity, price_offset)
VALUES (?, ?, ?, ?)
""";
KeyHolder kh = new GeneratedKeyHolder();
jdbcTemplate.update(con -> {
PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
ps.setLong(1, s.getProductId());
ps.setLong(2, s.getVariantValueId());
ps.setInt(3, s.getQuantity());
ps.setBigDecimal(4, s.getPriceOffset());
return ps;
}, kh);
return kh.getKey().longValue();
}

@Override
public boolean update(ProductVariantStock s) {
String sql = """
UPDATE product_variant_stock
SET quantity = ?, price_offset = ?
WHERE id = ?
""";
return jdbcTemplate.update(sql, s.getQuantity(), s.getPriceOffset(), s.getId()) > 0;
}

@Override
public Optional<ProductVariantStock> findById(Long id) {
String sql = "SELECT * FROM product_variant_stock WHERE id = ?";
List<ProductVariantStock> list =
jdbcTemplate.query(sql, new ProductVariantStockRowMapper(), id);
return list.stream().findFirst();
}

@Override
public Optional<ProductVariantStock> findByProductAndValue(Long productId, Long valueId) {
String sql = """
SELECT * FROM product_variant_stock
WHERE product_id = ? AND variant_value_id = ?
""";
List<ProductVariantStock> list =
jdbcTemplate.query(sql, new ProductVariantStockRowMapper(), productId, valueId);
return list.stream().findFirst();
}

@Override
public List<ProductVariantStock> findByProductId(Long productId) {
String sql = "SELECT * FROM product_variant_stock WHERE product_id = ?";
return jdbcTemplate.query(sql, new ProductVariantStockRowMapper(), productId);
}
}