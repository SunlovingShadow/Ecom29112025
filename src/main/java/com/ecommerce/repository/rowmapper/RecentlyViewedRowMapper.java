package com.ecommerce.repository.rowmapper;

import com.ecommerce.model.RecentlyViewed;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class RecentlyViewedRowMapper implements RowMapper<RecentlyViewed> {

    @Override
    public RecentlyViewed mapRow(ResultSet rs, int rowNum) throws SQLException {

        RecentlyViewed rv = new RecentlyViewed();

        rv.setId(rs.getLong("id"));
        rv.setUserId(rs.getLong("user_id"));
        rv.setProductId(rs.getLong("product_id"));

        // Convert viewed_at â†’ LocalDateTime
        if (rs.getTimestamp("viewed_at") != null) {
            rv.setViewedAt(rs.getTimestamp("viewed_at").toLocalDateTime());
        }

        return rv;
    }
}