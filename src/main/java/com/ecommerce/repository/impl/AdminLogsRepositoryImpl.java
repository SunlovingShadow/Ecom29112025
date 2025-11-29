package com.ecommerce.repository.impl;

import com.ecommerce.model.AdminLog;
import com.ecommerce.repository.AdminLogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class AdminLogsRepositoryImpl implements AdminLogsRepository {

    @Autowired
	private  JdbcTemplate jdbcTemplate;

    @Override
    public Long save(AdminLog log) {
        String sql = "INSERT INTO admin_logs (admin_user_id, action, created_at) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, log.getAdminUserId());
            ps.setString(2, log.getAction());
            ps.setTimestamp(3, Timestamp.valueOf(log.getCreatedAt() == null ? LocalDateTime.now() : log.getCreatedAt()));
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

	@Override
	public List<AdminLog> findRecent(int limit) {
		String sql="SELECT * FROM admin_logs ORDER BY created_at DESC LIMIT ?";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AdminLog.class),limit);
	}

	@Override
	public List<AdminLog> findAll() {
		String sql="SELECT * FROM admin_logs ORDER BY created_at DESC";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AdminLog.class));
	}
}