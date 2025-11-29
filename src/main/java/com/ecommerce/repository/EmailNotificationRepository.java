package com.ecommerce.repository;

import com.ecommerce.model.EmailNotification;

import java.util.List;
import java.util.Optional;

public interface EmailNotificationRepository {

    // Create new email notification (status = PENDING)
    Long save(EmailNotification notification);

    // Update email status (PENDING -> SENT / FAILED)
    boolean updateStatus(Long id, String status);

    // Get single email log
    Optional<EmailNotification> findById(Long id);

    // Get all email notifications of a user
    List<EmailNotification> findAllByUser(Long userId);

    // Admin: Fetch all notifications
    List<EmailNotification> findAll();
}