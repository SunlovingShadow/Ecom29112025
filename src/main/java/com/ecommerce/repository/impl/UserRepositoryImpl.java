package com.ecommerce.repository.impl;

import com.ecommerce.model.User;
import com.ecommerce.enums.AccountStatus;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.rowmapper.UserRowMapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ------------------------------------------------------------
    // SAVE USER
    // ------------------------------------------------------------
    @Override
    public Long save(User user) {
        String sql = """
            INSERT INTO users (username, email, password_hash, role, full_name, phone, account_status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole().name());
            ps.setString(5, user.getFullName());
            ps.setString(6, user.getPhone());
            ps.setString(7, user.getAccountStatus().name());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    // ------------------------------------------------------------
    // UPDATE USER
    // ------------------------------------------------------------
    @Override
    public boolean update(User user) {
        String sql = """
            UPDATE users SET 
                username = ?, 
                email = ?, 
                full_name = ?, 
                phone = ?, 
                account_status = ?, 
                role = ?
            WHERE id = ?
        """;

        return jdbcTemplate.update(sql,
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getAccountStatus().name(),
                user.getRole().name(),
                user.getId()
        ) > 0;
    }

    // ------------------------------------------------------------
    // SOFT DELETE USER (NO HARD DELETE ALLOWED)
    // account_status = SUSPENDED
    // ------------------------------------------------------------
    @Override
    public boolean softDelete(Long id) {
        String sql = """
            UPDATE users SET account_status = 'SUSPENDED' WHERE id = ?
        """;
        return jdbcTemplate.update(sql, id) > 0;
    }

    // ------------------------------------------------------------
    // UPDATE ACCOUNT STATUS
    // ------------------------------------------------------------
    @Override
    public boolean updateAccountStatus(Long id, String status) {
        String sql = """
            UPDATE users SET account_status = ? WHERE id = ?
        """;
        return jdbcTemplate.update(sql, status, id) > 0;
    }

    // ------------------------------------------------------------
    // FIND BY ID
    // ------------------------------------------------------------
    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        List<User> users = jdbcTemplate.query(sql, new UserRowMapper(), id);

        return users.stream().findFirst();
    }

    // ------------------------------------------------------------
    // FIND ACTIVE USER BY USERNAME 
    // For login â†’ only ACTIVE user allowed
    // ------------------------------------------------------------
    @Override
    public Optional<User> findActiveByUsername(String username) {
        String sql = """
            SELECT * FROM users 
            WHERE username = ? 
            AND account_status = 'ACTIVE'
        """;

        List<User> list = jdbcTemplate.query(sql, new UserRowMapper(), username);

        return list.stream().findFirst();
    }

    // ------------------------------------------------------------
    // FIND BY EMAIL
    // ------------------------------------------------------------
    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        List<User> list = jdbcTemplate.query(sql, new UserRowMapper(), email);

        return list.stream().findFirst();
    }

    // ------------------------------------------------------------
    // FIND ALL ACTIVE USERS
    // ------------------------------------------------------------
    @Override
    public List<User> findAllActive() {
        String sql = """
            SELECT * FROM users 
            WHERE account_status = 'ACTIVE'
        """;

        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    // ------------------------------------------------------------
    // FIND ALL USERS (ADMIN)
    // ACTIVE + PENDING + SUSPENDED
    // ------------------------------------------------------------
    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    // ------------------------------------------------------------
    // EXISTS BY EMAIL
    // ------------------------------------------------------------
    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    // ------------------------------------------------------------
    // EXISTS BY USERNAME
    // ------------------------------------------------------------
    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }
}