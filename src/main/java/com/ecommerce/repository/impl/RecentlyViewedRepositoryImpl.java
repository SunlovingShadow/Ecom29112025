package com.ecommerce.repository.impl;

import com.ecommerce.model.RecentlyViewed;
import com.ecommerce.repository.RecentlyViewedRepository;
import com.ecommerce.repository.rowmapper.RecentlyViewedRowMapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RecentlyViewedRepositoryImpl implements RecentlyViewedRepository {

    private final JdbcTemplate jdbcTemplate;

    public RecentlyViewedRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ------------------------------------------------------------
    // ADD OR UPDATE RECENTLY VIEWED ENTRY
    // ------------------------------------------------------------
    @Override
    public void saveOrUpdate(Long userId, Long productId) {

        // If exists â†’ update timestamp
        String updateSql = """
            UPDATE recently_viewed
            SET viewed_at = CURRENT_TIMESTAMP
            WHERE user_id = ? AND product_id = ?
        """;

        int updated = jdbcTemplate.update(updateSql, userId, productId);

        // If not updated (meaning record doesn't exist) â†’ insert new record
        if (updated == 0) {
            String insertSql = """
                INSERT INTO recently_viewed (user_id, product_id)
                VALUES (?, ?)
            """;
            jdbcTemplate.update(insertSql, userId, productId);
        }

        // Keep only last 10 items
        String cleanupSql = """
            DELETE FROM recently_viewed
            WHERE user_id = ?
              AND id NOT IN (
                    SELECT id FROM (
                        SELECT id FROM recently_viewed
                        WHERE user_id = ?
                        ORDER BY viewed_at DESC
                        LIMIT 10
                    ) AS t
              )
        """;

        jdbcTemplate.update(cleanupSql, userId, userId);
    }

    // ------------------------------------------------------------
    // GET LAST N RECENTLY VIEWED PRODUCTS FOR USER
    // ------------------------------------------------------------
    @Override
    public List<RecentlyViewed> findLastNByUser(Long userId, int limit) {

        String sql = """
            SELECT * FROM recently_viewed
            WHERE user_id = ?
            ORDER BY viewed_at DESC
            LIMIT ?
        """;

        return jdbcTemplate.query(sql, new RecentlyViewedRowMapper(), userId, limit);
    }

    // ------------------------------------------------------------
    // CLEAR ENTIRE RECENTLY VIEWED HISTORY OF USER
    // ------------------------------------------------------------
    @Override
    public boolean deleteAllByUser(Long userId) {

        String sql = """
            DELETE FROM recently_viewed
            WHERE user_id = ?
        """;

        return jdbcTemplate.update(sql, userId) > 0;
    }
}