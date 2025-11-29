package com.ecommerce.repository.impl;

import com.ecommerce.model.Payment;
import com.ecommerce.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    @Autowired
    private JdbcTemplate jdbc;

    @Override
    public Payment save(Payment payment) {
        String sql = "INSERT INTO payments (order_id, amount, method, status, txn_reference, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, payment.getOrderId());
            ps.setBigDecimal(2, payment.getAmount());
            ps.setString(3, payment.getMethod());
            ps.setString(4, payment.getStatus());
            ps.setString(5, payment.getTxnReference());
            return ps;
        }, keyHolder);

        payment.setId(keyHolder.getKey().longValue());
        return payment;
    }
}