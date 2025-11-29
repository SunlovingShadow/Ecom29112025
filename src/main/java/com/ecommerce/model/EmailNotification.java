package com.ecommerce.model;

import java.time.LocalDateTime;

/**
 * Entity representing an email notification entry.
 * Stores emails that must be sent or were attempted.
 */
public class EmailNotification {

    private Long id;               // Primary key
    private Long userId;           // User receiving the email
    private String subject;        // Email subject
    private String message;        // Email body content
    private String status;         // PENDING / SENT / FAILED
    private LocalDateTime createdAt; // When the entry was created

    // -------- Getters & Setters --------

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

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

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}