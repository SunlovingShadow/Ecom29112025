package com.ecommerce.repository.rowmapper;

import com.ecommerce.model.EmailNotification;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class EmailNotificationRowMapper implements RowMapper<EmailNotification> {

    @Override
    public EmailNotification mapRow(ResultSet rs, int rowNum) throws SQLException {

        EmailNotification en = new EmailNotification();

        en.setId(rs.getLong("id"));
        en.setUserId(rs.getLong("user_id"));
        en.setSubject(rs.getString("subject"));
        en.setMessage(rs.getString("message"));
        en.setStatus(rs.getString("status"));

        // Convert SQL TIMESTAMP â†’ LocalDateTime safely
        if (rs.getTimestamp("created_at") != null) {
            en.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }

        return en;
    }
}