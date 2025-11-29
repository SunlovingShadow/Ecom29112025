package com.ecommerce.dto;

/**
 * DTO used when user/admin wants to send an email.
 * Contains subject + message only.
 */
public class EmailSendRequest {

    private String subject;
    private String message;

    // -------- Getters & Setters --------

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}