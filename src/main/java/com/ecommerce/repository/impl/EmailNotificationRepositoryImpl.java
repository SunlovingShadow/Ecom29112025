package com.ecommerce.repository.impl;

import com.ecommerce.model.EmailNotification;
import com.ecommerce.repository.EmailNotificationRepository;
import com.ecommerce.repository.rowmapper.EmailNotificationRowMapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class EmailNotificationRepositoryImpl implements EmailNotificationRepository {

    private final JdbcTemplate jdbcTemplate;

    public EmailNotificationRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ------------------------------------------------------------
    // SAVE NEW EMAIL NOTIFICATION
    // ------------------------------------------------------------
    @Override
    public Long save(EmailNotification notification) {

        String sql = """
            INSERT INTO email_notifications (
                user_id, subject, message, status
            ) VALUES (?, ?, ?, ?)
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps =
                    con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, notification.getUserId());
            ps.setString(2, notification.getSubject());
            ps.setString(3, notification.getMessage());
            ps.setString(4, notification.getStatus());   // PENDING initially
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    // ------------------------------------------------------------
    // UPDATE EMAIL SEND STATUS
    // ------------------------------------------------------------
    @Override
    public boolean updateStatus(Long id, String status) {

        String sql = """
            UPDATE email_notifications
            SET status = ?
            WHERE id = ?
        """;

        return jdbcTemplate.update(sql, status, id) > 0;
    }

    // ------------------------------------------------------------
    // FIND SINGLE EMAIL ENTRY BY ID
    // ------------------------------------------------------------
    @Override
    public Optional<EmailNotification> findById(Long id) {

        String sql = """
            SELECT * FROM email_notifications
            WHERE id = ?
        """;

        List<EmailNotification> list =
                jdbcTemplate.query(sql, new EmailNotificationRowMapper(), id);

        return list.stream().findFirst();
    }

    // ------------------------------------------------------------
    // GET ALL EMAIL NOTIFICATIONS OF ONE USER
    // ------------------------------------------------------------
    @Override
    public List<EmailNotification> findAllByUser(Long userId) {

        String sql = """
            SELECT * FROM email_notifications
            WHERE user_id = ?
            ORDER BY created_at DESC
        """;

        return jdbcTemplate.query(sql, new EmailNotificationRowMapper(), userId);
    }

    // ------------------------------------------------------------
    // ADMIN â€” GET ALL EMAIL LOGS
    // ------------------------------------------------------------
    @Override
    public List<EmailNotification> findAll() {

        String sql = """
            SELECT * FROM email_notifications
            ORDER BY created_at DESC
        """;

        return jdbcTemplate.query(sql, new EmailNotificationRowMapper());
    }
}