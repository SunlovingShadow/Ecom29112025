package com.ecommerce.repository.impl;

import com.ecommerce.model.ProductVariantGroup;
import com.ecommerce.repository.ProductVariantGroupRepository;
import com.ecommerce.repository.rowmapper.ProductVariantGroupRowMapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductVariantGroupRepositoryImpl implements ProductVariantGroupRepository {

private final JdbcTemplate jdbcTemplate;

public ProductVariantGroupRepositoryImpl(JdbcTemplate jdbcTemplate) {
this.jdbcTemplate = jdbcTemplate;
}

@Override
public Long save(ProductVariantGroup g) {
String sql = """
INSERT INTO product_variant_group (product_id, group_name)
VALUES (?, ?)
""";
KeyHolder kh = new GeneratedKeyHolder();
jdbcTemplate.update(con -> {
PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
ps.setLong(1, g.getProductId());
ps.setString(2, g.getGroupName());
return ps;
}, kh);
return kh.getKey().longValue();
}

@Override
public boolean update(ProductVariantGroup g) {
String sql = """
UPDATE product_variant_group
SET group_name = ?
WHERE id = ?
""";
return jdbcTemplate.update(sql, g.getGroupName(), g.getId()) > 0;
}

@Override
public boolean delete(Long id) {
String sql = "DELETE FROM product_variant_group WHERE id = ?";
return jdbcTemplate.update(sql, id) > 0;
}

@Override
public Optional<ProductVariantGroup> findById(Long id) {
String sql = "SELECT * FROM product_variant_group WHERE id = ?";
List<ProductVariantGroup> list =
jdbcTemplate.query(sql, new ProductVariantGroupRowMapper(), id);
return list.stream().findFirst();
}

@Override
public List<ProductVariantGroup> findByProductId(Long productId) {
String sql = "SELECT * FROM product_variant_group WHERE product_id = ?";
return jdbcTemplate.query(sql, new ProductVariantGroupRowMapper(), productId);
}
}