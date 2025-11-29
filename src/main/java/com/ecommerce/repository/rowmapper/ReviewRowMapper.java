package com.ecommerce.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ecommerce.model.Review;

public class ReviewRowMapper implements RowMapper<Review>{

	@Override
	public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Review r=new Review();
		r.setId(rs.getLong("id"));
		r.setProductId(rs.getLong("product_id"));
        r.setUserId(rs.getLong("user_id"));
        r.setRating(rs.getInt("rating"));
        r.setTitle(rs.getString("title"));
        r.setBody(rs.getString("body"));
        r.setDeleted(rs.getBoolean("is_deleted"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        return r;
	}

}