package com.ecommerce.repository.impl;

import com.ecommerce.model.ReturnRequest;
import com.ecommerce.repository.ReturnRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Repository
public class ReturnRepositoryImpl implements ReturnRepository {

    private static final Logger log = LoggerFactory.getLogger(ReturnRepositoryImpl.class);

    private final JdbcTemplate jdbc;

    // Constructor Injection
    public ReturnRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        log.info("ReturnRepository initialized");
    }

    // ════════════════════════════════════════════════════════════════════════
    // SAVE RETURN REQUEST
    // ════════════════════════════════════════════════════════════════════════
    
    @Override
    public void save(ReturnRequest req) {
        log.debug("Saving return request for order: {}", req.getOrderId());
        
        String sql = "INSERT INTO return_requests (order_id, reason, status, created_at) VALUES (?, ?, 'REQUESTED', NOW())";
        
        try {
            int rows = jdbc.update(sql, req.getOrderId(), req.getReason());
            log.info("Return request saved. Rows affected: {}", rows);
        } catch (Exception e) {
            log.error("Failed to save return request for order {}: {}", req.getOrderId(), e.getMessage());
            throw e;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // FIND BY USER ID
    // ════════════════════════════════════════════════════════════════════════
    
    @Override
    public List<ReturnRequest> findByUserId(Long userId) {
        log.debug("Finding return requests for user: {}", userId);
        
        String sql = """
            SELECT rr.id, rr.order_id, rr.reason, rr.status, rr.created_at 
            FROM return_requests rr
            JOIN orders o ON rr.order_id = o.id
            WHERE o.user_id = ?
            ORDER BY rr.created_at DESC
        """;
        
        try {
            List<ReturnRequest> results = jdbc.query(sql, rowMapper, userId);
            log.debug("Found {} return requests for user: {}", results.size(), userId);
            return results;
        } catch (Exception e) {
            log.error("Failed to find return requests for user {}: {}", userId, e.getMessage());
            return Collections.emptyList();
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // CHECK IF RETURN EXISTS FOR ORDER
    // ════════════════════════════════════════════════════════════════════════
    
    @Override
    public boolean existsByOrderId(Long orderId) {
        log.debug("Checking if return exists for order: {}", orderId);
        
        String sql = "SELECT COUNT(*) FROM return_requests WHERE order_id = ?";
        
        try {
            Integer count = jdbc.queryForObject(sql, Integer.class, orderId);
            boolean exists = count != null && count > 0;
            log.debug("Return exists for order {}: {}", orderId, exists);
            return exists;
        } catch (Exception e) {
            log.error("Failed to check return existence for order {}: {}", orderId, e.getMessage());
            return false;
        }
    }
    
 // ════════════════════════════════════════════════════════════════════════
 // FIND RETURN REQUEST BY ORDER ID
 // ════════════════════════════════════════════════════════════════════════

 @Override
 public Optional<ReturnRequest> findByOrderId(Long orderId) {
     log.debug("Finding return request for order: {}", orderId);
     
     String sql = """
         SELECT id, order_id, reason, status, created_at 
         FROM return_requests 
         WHERE order_id = ?
     """;
     
     try {
         ReturnRequest result = jdbc.queryForObject(sql, rowMapper, orderId);
         log.debug("Found return request for order: {}", orderId);
         return Optional.ofNullable(result);
     } catch (org.springframework.dao.EmptyResultDataAccessException e) {
         log.debug("No return request found for order: {}", orderId);
         return Optional.empty();
     }
 }
    

    // ════════════════════════════════════════════════════════════════════════
    // ROW MAPPER - NULL-SAFE
    // ════════════════════════════════════════════════════════════════════════
    
    private final RowMapper<ReturnRequest> rowMapper = (rs, rowNum) -> {
        ReturnRequest rr = new ReturnRequest();
        
        rr.setId(rs.getLong("id"));
        rr.setOrderId(rs.getLong("order_id"));
        rr.setReason(rs.getString("reason"));
        rr.setStatus(rs.getString("status"));
        
        // Null-safe timestamp handling
        Timestamp ts = rs.getTimestamp("created_at");
        rr.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
        
        return rr;
    };
}