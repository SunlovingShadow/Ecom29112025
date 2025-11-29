package com.ecommerce.service.impl;

import com.ecommerce.dto.EmailNotificationResponse;
import com.ecommerce.dto.EmailSendRequest;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.EmailNotification;
import com.ecommerce.repository.EmailNotificationRepository;
import com.ecommerce.service.EmailNotificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailNotificationServiceImpl implements EmailNotificationService {

	private static final Logger log = LoggerFactory.getLogger(EmailNotificationServiceImpl.class);

	private final EmailNotificationRepository emailRepo;

	public EmailNotificationServiceImpl(EmailNotificationRepository repo) {
		this.emailRepo = repo;
	}

	@Override
	public EmailNotificationResponse sendEmail(Long userId, EmailSendRequest request) {

		if (request.getSubject() == null || request.getSubject().isBlank()) {
			throw new BadRequestException("Subject cannot be empty");
		}
		if (request.getMessage() == null || request.getMessage().isBlank()) {
			throw new BadRequestException("Message cannot be empty");
		}

		EmailNotification n = new EmailNotification();
		n.setUserId(userId);
		n.setSubject(request.getSubject());
		n.setMessage(request.getMessage());
		n.setStatus("PENDING");

		Long id = emailRepo.save(n);
		n.setId(id);

		// ---------------------------------------------------------------------------------------
		// MOá¸° MODE â€” Instead of sending real email, we log it
		// ---------------------------------------------------------------------------------------
		log.info("MOCK EMAIL â†’ USER: {}, SUBJECT: {}, MESSAGE: {}", userId, request.getSubject(),
				request.getMessage());

		System.out.println("\n=== MOCK EMAIL ===");
		System.out.println("To User: " + userId);
		System.out.println("Subject: " + request.getSubject());
		System.out.println("Message: " + request.getMessage());
		System.out.println("==================\n");

		// mark as SENT
		emailRepo.updateStatus(id, "SENT");
		n.setStatus("SENT");

		return map(n);
	}

	@Override
	public boolean updateStatus(Long id, String status) {
		return emailRepo.updateStatus(id, status);
	}

	@Override
	public EmailNotificationResponse getNotificationById(Long id) {
		EmailNotification n = emailRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Email notification not found"));
		return map(n);
	}

	@Override
	public List<EmailNotificationResponse> getAllByUser(Long userId) {
		return emailRepo.findAllByUser(userId).stream().map(this::map).collect(Collectors.toList());
	}

	@Override
	public List<EmailNotificationResponse> getAllNotifications() {
		return emailRepo.findAll().stream().map(this::map).collect(Collectors.toList());
	}

	private EmailNotificationResponse map(EmailNotification n) {
		EmailNotificationResponse r = new EmailNotificationResponse();
		r.setId(n.getId());
		r.setUserId(n.getUserId());
		r.setSubject(n.getSubject());
		r.setMessage(n.getMessage());
		r.setStatus(n.getStatus());
		r.setCreatedAt(n.getCreatedAt());
		return r;
	}
}