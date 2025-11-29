package com.ecommerce.service;

import com.ecommerce.dto.EmailNotificationResponse;
import com.ecommerce.dto.EmailSendRequest;

import java.util.List;

public interface EmailNotificationService {

    // Create an email notification (PENDING)
    EmailNotificationResponse sendEmail(Long userId, EmailSendRequest request);

    // Update email status (SENT or FAILED)
    boolean updateStatus(Long notificationId, String status);

    // Get single email log by ID
    EmailNotificationResponse getNotificationById(Long id);

    // Get user's all email logs
    List<EmailNotificationResponse> getAllByUser(Long userId);

    // Admin: get all email logs
    List<EmailNotificationResponse> getAllNotifications();
}