package com.ecommerce.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.RowMapper;

import com.ecommerce.model.User;
import com.ecommerce.enums.UserRole;
import com.ecommerce.enums.AccountStatus;

public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {

        User user = new User();

        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));

        // Enum mapping
        String roleStr = rs.getString("role");
        if (roleStr != null) {
            user.setRole(UserRole.valueOf(roleStr));
        }

        user.setFullName(rs.getString("full_name"));
        user.setPhone(rs.getString("phone"));

        // AccountStatus enum mapping
        String accStatus = rs.getString("account_status");
        if (accStatus != null) {
            user.setAccountStatus(AccountStatus.valueOf(accStatus));
        }

        // Timestamp â†’ LocalDateTime
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
        user.setCreatedAt(createdAt);

        return user;
    }
}