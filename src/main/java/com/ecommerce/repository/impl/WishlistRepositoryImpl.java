package com.ecommerce.repository.impl;

import com.ecommerce.repository.rowmapper.WishlistRowMapper;
import com.ecommerce.model.WishlistItem;
import com.ecommerce.repository.WishlistRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class WishlistRepositoryImpl implements WishlistRepository {

    @Autowired
	private JdbcTemplate jdbcTemplate;

    @Override
    public WishlistItem add(Long userId, Long productId) {
        String sql = "INSERT INTO wishlist_items (user_id, product_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, productId);

        return findItem(userId, productId).orElse(null);
    }

    @Override
    public boolean remove(Long userId, Long productId) {
        String sql = "DELETE FROM wishlist_items WHERE user_id = ? AND product_id = ?";
        return jdbcTemplate.update(sql, userId, productId) > 0;
    }

    @Override
    public List<WishlistItem> findByUserId(Long userId) {
        String sql = "SELECT * FROM wishlist_items WHERE user_id = ?";
        return jdbcTemplate.query(sql, new WishlistRowMapper(), userId);
    }

    @Override
    public Optional<WishlistItem> findItem(Long userId, Long productId) {
        String sql = "SELECT * FROM wishlist_items WHERE user_id = ? AND product_id = ?";
        List<WishlistItem> list = jdbcTemplate.query(sql, new WishlistRowMapper(), userId, productId);
        return list.stream().findFirst();
    }

}