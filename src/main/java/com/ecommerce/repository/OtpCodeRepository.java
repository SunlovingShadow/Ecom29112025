package com.ecommerce.repository;

import com.ecommerce.model.OtpCode;

import java.util.Optional;

public interface OtpCodeRepository {

    // Save new OTP to database
    Long save(OtpCode otp);

    // Fetch latest active OTP for a user (identifier)
    Optional<OtpCode> findLatestValidOtp(String identifier);

    // Mark OTP as used
    boolean markOtpUsed(Long id);

    // Delete old OTPs for the same user/identifier
    boolean deleteOldOtps(String identifier);
}