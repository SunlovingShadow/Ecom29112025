package com.ecommerce.dto;

/**
 * DTO used when user submits OTP for verification.
 * Contains identifier (email/phone) + OTP code.
 */
public class OtpVerifyRequest {

    private String identifier;  // Email or phone used during OTP generation
    private String otp;         // OTP entered by user

    // -------- Getters & Setters --------

    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getOtp() {
        return otp;
    }
    public void setOtp(String otp) {
        this.otp = otp;
    }
}