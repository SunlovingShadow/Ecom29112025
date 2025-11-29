package com.ecommerce.service;

import com.ecommerce.dto.OtpRequest;
import com.ecommerce.dto.OtpVerifyRequest;

public interface OtpService {

    // Generate OTP for any identifier (email/phone)
    String generateOtp(OtpRequest request);

    // Verify OTP
    String verifyOtp(OtpVerifyRequest request);
}