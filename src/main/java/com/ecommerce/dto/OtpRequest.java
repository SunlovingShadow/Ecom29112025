package com.ecommerce.dto;

/**
 * DTO used when user requests OTP generation.
 * Identifier can be email or phone number.
 */
public class OtpRequest {

    private String identifier;   // Email or Phone Number

    // -------- Getters & Setters --------

    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}