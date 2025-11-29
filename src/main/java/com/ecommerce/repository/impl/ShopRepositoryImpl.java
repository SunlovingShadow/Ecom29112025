package com.ecommerce.repository.impl;

import com.ecommerce.model.Shop;
import com.ecommerce.repository.ShopRepository;
import com.ecommerce.repository.rowmapper.ShopRowMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ShopRepositoryImpl implements ShopRepository {

    @Autowired
	private  JdbcTemplate jdbcTemplate;
    
    private  final ShopRowMapper mapper=new ShopRowMapper() ;


    @Override
    public Long save(Shop shop) {
        String sql = "INSERT INTO shops (owner_user_id, name, description, address, is_approved, is_active, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                shop.getOwnerUserId(),
                shop.getName(),
                shop.getDescription(),
                shop.getAddress(),
                shop.getIsApproved(),
                shop.getIsActive(),
                Timestamp.valueOf(LocalDateTime.now())
        );
        // return last inserted id (simple approach)
        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        return id;
    }

    @Override
    public Shop update(Shop shop) {
        String sql = "UPDATE shops SET name=?, description=?, address=?, is_approved=?, is_active=? WHERE id=? AND is_active=1";
        int rows = jdbcTemplate.update(sql,
                shop.getName(),
                shop.getDescription(),
                shop.getAddress(),
                shop.getIsApproved(),
                shop.getIsActive(),
                shop.getId()
        );
        return rows > 0 ? shop : null;
    }

    @Override
    public Optional<Shop> getShopById(Long id) {
        String sql = "SELECT * FROM shops WHERE id=? AND is_active=1";
        List<Shop> list = jdbcTemplate.query(sql, mapper, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public List<Shop> getAllShops() {
        String sql = "SELECT * FROM shops WHERE is_active=TRUE";
        return jdbcTemplate.query(sql, mapper);
    }

    @Override
    public List<Shop> getPendingApprovalShops() {
        String sql = "SELECT * FROM shops WHERE is_approved=0 AND is_active=1";
        return jdbcTemplate.query(sql, mapper);
    }

    @Override
    public boolean approveShop(Long shopId) {
        String sql = "UPDATE shops SET is_approved=1 WHERE id=? AND is_active=1";
        return jdbcTemplate.update(sql, shopId) > 0;
    }

    @Override
    public boolean rejectShop(Long shopId) {
        String sql = "UPDATE shops SET is_approved=0, is_active=0, WHERE id=? AND is_active=1";
        return jdbcTemplate.update(sql, shopId) > 0;
    }

    @Override
    public boolean softDeleteShop(Long shopId) {
        String sql = "UPDATE shops SET is_active=0 WHERE id=?";
        return jdbcTemplate.update(sql, shopId) > 0;
    }

    @Override
    public List<Shop> findByOwner(Long ownerUserId) {
        String sql = "SELECT * FROM shops WHERE owner_user_id=? AND is_active=1";
        return jdbcTemplate.query(sql, mapper, ownerUserId);
    }
}