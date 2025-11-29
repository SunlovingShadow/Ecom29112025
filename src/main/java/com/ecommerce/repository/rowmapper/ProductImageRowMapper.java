package com.ecommerce.repository.rowmapper;

import com.ecommerce.model.ProductImage;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductImageRowMapper implements RowMapper<ProductImage> {

    @Override
    public ProductImage mapRow(ResultSet rs, int rowNum) throws SQLException {

        ProductImage image = new ProductImage();

        image.setId(rs.getLong("id"));
        image.setProductId(rs.getLong("product_id"));
        image.setImagePath(rs.getString("image_path"));
        image.setPrimary(rs.getBoolean("is_primary"));
        image.setSortImageOrder(rs.getInt("sort_image_order"));
        image.setDeleted(rs.getBoolean("is_deleted"));

        return image;
    }
}
