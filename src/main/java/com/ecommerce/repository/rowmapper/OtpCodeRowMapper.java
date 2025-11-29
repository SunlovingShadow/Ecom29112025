package com.ecommerce.repository.rowmapper;

import com.ecommerce.model.OtpCode;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class OtpCodeRowMapper implements RowMapper<OtpCode> {

    @Override
    public OtpCode mapRow(ResultSet rs, int rowNum) throws SQLException {

        OtpCode otp = new OtpCode();

        otp.setId(rs.getLong("id"));
        otp.setIdentifier(rs.getString("identifier"));
        otp.setOtpCode(rs.getString("otp_code"));

        // Convert TIMESTAMP to LocalDateTime safely
        if (rs.getTimestamp("expires_at") != null) {
            otp.setExpiresAt(rs.getTimestamp("expires_at").toLocalDateTime());
        }

        otp.setUsed(rs.getBoolean("is_used"));

        if (rs.getTimestamp("created_at") != null) {
            otp.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }

        return otp;
    }
}