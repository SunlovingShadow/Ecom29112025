package com.ecommerce.controller;

import com.ecommerce.dto.EmailNotificationResponse;
import com.ecommerce.dto.EmailSendRequest;
import com.ecommerce.service.EmailNotificationService;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling email notification requests.
 */
@RestController
@RequestMapping("/api/email")
public class EmailNotificationController {

    private final EmailNotificationService emailService;

    public EmailNotificationController(EmailNotificationService emailService) {
        this.emailService = emailService;
    }

    // ------------------------------------------------------------
    // SEND EMAIL (PENDING â†’ SENT)
    // ------------------------------------------------------------
    @PostMapping("/send")
    public ResponseEntity<EmailNotificationResponse> sendEmail(
            @RequestParam Long userId,
            @RequestBody EmailSendRequest request
    ) {
        EmailNotificationResponse response = emailService.sendEmail(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ------------------------------------------------------------
    // GET EMAIL BY ID
    // ------------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<EmailNotificationResponse> getEmailById(@PathVariable Long id) {
        return ResponseEntity.ok(emailService.getNotificationById(id));
    }

    // ------------------------------------------------------------
    // GET ALL EMAIL NOTIFICATIONS OF A USER
    // ------------------------------------------------------------
    @GetMapping("/user")
    public ResponseEntity<List<EmailNotificationResponse>> getEmailsForUser(
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(emailService.getAllByUser(userId));
    }

    // ------------------------------------------------------------
    // ADMIN â€” GET ALL EMAIL LOGS
    // ------------------------------------------------------------
    @GetMapping("/admin/all")
    public ResponseEntity<List<EmailNotificationResponse>> getAllEmailLogs() {
        return ResponseEntity.ok(emailService.getAllNotifications());
    }

    // ------------------------------------------------------------
    // UPDATE EMAIL STATUS MANUALLY (OPTIONAL)
    // ------------------------------------------------------------
    @PutMapping("/status")
    public ResponseEntity<String> updateStatus(
            @RequestParam Long id,
            @RequestParam String status
    ) {
        boolean updated = emailService.updateStatus(id, status);

        if (updated) {
            return ResponseEntity.ok("Email status updated to: " + status);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Failed to update status");
    }
}