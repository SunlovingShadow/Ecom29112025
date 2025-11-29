package com.ecommerce.repository.impl;

import com.ecommerce.model.ProductVariantValue;
import com.ecommerce.repository.ProductVariantValueRepository;
import com.ecommerce.repository.rowmapper.ProductVariantValueRowMapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductVariantValueRepositoryImpl implements ProductVariantValueRepository {

private final JdbcTemplate jdbcTemplate;

public ProductVariantValueRepositoryImpl(JdbcTemplate jdbcTemplate) {
this.jdbcTemplate = jdbcTemplate;
}

@Override
public Long save(ProductVariantValue v) {
String sql = """
INSERT INTO product_variant_value (group_id, value_name)
VALUES (?, ?)
""";
KeyHolder kh = new GeneratedKeyHolder();
jdbcTemplate.update(con -> {
PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
ps.setLong(1, v.getGroupId());
ps.setString(2, v.getValueName());
return ps;
}, kh);
return kh.getKey().longValue();
}

@Override
public boolean update(ProductVariantValue v) {
String sql = """
UPDATE product_variant_value
SET value_name = ?
WHERE id = ?
""";
return jdbcTemplate.update(sql, v.getValueName(), v.getId()) > 0;
}

@Override
public boolean delete(Long id) {
String sql = "DELETE FROM product_variant_value WHERE id = ?";
return jdbcTemplate.update(sql, id) > 0;
}

@Override
public Optional<ProductVariantValue> findById(Long id) {
String sql = "SELECT * FROM product_variant_value WHERE id = ?";
List<ProductVariantValue> list =
jdbcTemplate.query(sql, new ProductVariantValueRowMapper(), id);
return list.stream().findFirst();
}

@Override
public List<ProductVariantValue> findByGroupId(Long groupId) {
String sql = "SELECT * FROM product_variant_value WHERE group_id = ?";
return jdbcTemplate.query(sql, new ProductVariantValueRowMapper(), groupId);
}
}