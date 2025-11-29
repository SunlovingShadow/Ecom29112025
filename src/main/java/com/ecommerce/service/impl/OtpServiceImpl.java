package com.ecommerce.service.impl;

import com.ecommerce.dto.OtpRequest;
import com.ecommerce.dto.OtpVerifyRequest;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.OtpCode;
import com.ecommerce.repository.OtpCodeRepository;
import com.ecommerce.service.EmailNotificationService;
import com.ecommerce.service.OtpService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpServiceImpl implements OtpService {

	private static final Logger log = LoggerFactory.getLogger(OtpServiceImpl.class);

	private final OtpCodeRepository otpRepo;
	private final EmailNotificationService emailService;

	public OtpServiceImpl(OtpCodeRepository otpRepo, EmailNotificationService emailService) {
		this.otpRepo = otpRepo;
		this.emailService = emailService;
	}

	// ------------------------------------------------------------
	// GENERATE OTP (NOW USING LOGGER INSTEAD OF EMAIL)
	// ------------------------------------------------------------
	@Override
	public String generateOtp(OtpRequest request) {

		if (request.getIdentifier() == null || request.getIdentifier().trim().isEmpty()) {
			throw new BadRequestException("Identifier cannot be empty");
		}

		String identifier = request.getIdentifier().trim();

		// Remove old OTPs
		otpRepo.deleteOldOtps(identifier);

		// Generate 4-digit OTP
		String otp = String.valueOf(1000 + new Random().nextInt(9000));

		OtpCode otpCode = new OtpCode();
		otpCode.setIdentifier(identifier);
		otpCode.setOtpCode(otp);
		otpCode.setUsed(false);
		otpCode.setExpiresAt(LocalDateTime.now().plusMinutes(5));

		otpRepo.save(otpCode);

		// **MOCK MODE: SHOW OTP IN CONSOLE**
		log.info("Generated OTP for {} :’ {}", identifier, otp);
		System.out.println("DEBUG OTP for " + identifier + " = " + otp);

		return "OTP generated successfully";
	}

	// ------------------------------------------------------------
	// VERIFY OTP
	// ------------------------------------------------------------
	@Override
	public String verifyOtp(OtpVerifyRequest request) {

		if (request.getIdentifier() == null || request.getIdentifier().isEmpty()) {
			throw new BadRequestException("Identifier cannot be empty");
		}
		if (request.getOtp() == null || request.getOtp().isEmpty()) {
			throw new BadRequestException("OTP cannot be empty");
		}

		String identifier = request.getIdentifier();
		String otpInput = request.getOtp();

		OtpCode otpRecord = otpRepo.findLatestValidOtp(identifier)
				.orElseThrow(() -> new ResourceNotFoundException("OTP expired or not found"));

		if (otpRecord.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new BadRequestException("OTP expired");
		}

		if (!otpRecord.getOtpCode().equals(otpInput)) {
			throw new BadRequestException("Invalid OTP");
		}

		otpRepo.markOtpUsed(otpRecord.getId());

		return "OTP verified successfully!";
	}
}