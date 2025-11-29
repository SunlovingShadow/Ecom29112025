package com.ecommerce.model;

import java.time.LocalDateTime;

/**
 * Entity representing OTP codes used for login/verification.
 */
public class OtpCode {

    private Long id;                 // Primary key
    private String identifier;       // Email or phone number (unique per OTP)
    private String otpCode;          // The 4-6 digit OTP
    private LocalDateTime expiresAt; // OTP expiry timestamp
    private boolean isUsed;          // OTP already used?
    private LocalDateTime createdAt; // Time of OTP generation

    // -------- Getters & Setters --------

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getOtpCode() {
        return otpCode;
    }
    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isUsed() {
        return isUsed;
    }
    public void setUsed(boolean used) {
        isUsed = used;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}