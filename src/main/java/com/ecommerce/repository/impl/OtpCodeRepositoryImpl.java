package com.ecommerce.repository.impl;

import com.ecommerce.model.OtpCode;
import com.ecommerce.repository.OtpCodeRepository;
import com.ecommerce.repository.rowmapper.OtpCodeRowMapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class OtpCodeRepositoryImpl implements OtpCodeRepository {

    private final JdbcTemplate jdbcTemplate;

    public OtpCodeRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ------------------------------------------------------------
    // SAVE NEW OTP ENTRY
    // ------------------------------------------------------------
    @Override
    public Long save(OtpCode otp) {

        String sql = """
            INSERT INTO otp_codes (
                identifier,
                otp_code,
                expires_at,
                is_used
            ) VALUES (?, ?, ?, ?)
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps =
                connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, otp.getIdentifier());
            ps.setString(2, otp.getOtpCode());
            ps.setTimestamp(3, java.sql.Timestamp.valueOf(otp.getExpiresAt()));
            ps.setBoolean(4, otp.isUsed());

            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    // ------------------------------------------------------------
    // FIND LATEST VALID OTP FOR AN IDENTIFIER
    // Only latest unused and unexpired OTP should be returned
    // ------------------------------------------------------------
    @Override
    public Optional<OtpCode> findLatestValidOtp(String identifier) {

        String sql = """
            SELECT * FROM otp_codes
            WHERE identifier = ?
              AND is_used = FALSE
              AND expires_at >= NOW()
            ORDER BY created_at DESC
            LIMIT 1
        """;

        List<OtpCode> list = jdbcTemplate.query(
                sql, new OtpCodeRowMapper(), identifier
        );

        return list.stream().findFirst();
    }

    // ------------------------------------------------------------
    // MARK OTP AS USED (prevent reuse)
    // ------------------------------------------------------------
    @Override
    public boolean markOtpUsed(Long id) {

        String sql = """
            UPDATE otp_codes
            SET is_used = TRUE
            WHERE id = ?
        """;

        return jdbcTemplate.update(sql, id) > 0;
    }

    // ------------------------------------------------------------
    // DELETE OLD OTPs FOR SAME IDENTIFIER
    // Keeps DB clean, ensures only one active otp remains
    // ------------------------------------------------------------
    @Override
    public boolean deleteOldOtps(String identifier) {

        String sql = """
            DELETE FROM otp_codes
            WHERE identifier = ?
        """;

        return jdbcTemplate.update(sql, identifier) > 0;
    }
}