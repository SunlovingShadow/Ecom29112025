package com.ecommerce.service.impl;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.CouponRequest;
import com.ecommerce.dto.CouponResponse;
import com.ecommerce.enums.DiscountType;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.Coupon;
import com.ecommerce.repository.CouponRepository;
import com.ecommerce.repository.ShopRepository;
import com.ecommerce.service.CouponService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponServiceImpl implements CouponService {

    private static final Logger log = LoggerFactory.getLogger(CouponServiceImpl.class);

    private final CouponRepository couponRepository;
    private final ShopRepository shopRepository;

    // Constructor Injection
    public CouponServiceImpl(CouponRepository couponRepository, ShopRepository shopRepository) {
        this.couponRepository = couponRepository;
        this.shopRepository = shopRepository;
        log.info("CouponService initialized");
    }

   

    @Override
    @Transactional
    public CouponResponse createCoupon(CouponRequest req) {
        log.info("Creating coupon with code: {}", req != null ? req.getCode() : "NULL");
        
        // ─────────────────────────────────────────────────────────────────────
        // VALIDATION
        // ─────────────────────────────────────────────────────────────────────
        
        validateCouponRequest(req);
        
        // Edge Case: Duplicate code
        String code = req.getCode().toUpperCase().trim();
        if (couponRepository.findByCode(code).isPresent()) {
            log.error("Coupon code already exists: {}", code);
            throw new BadRequestException("Coupon code '" + code + "' already exists");
        }
        
        // ─────────────────────────────────────────────────────────────────────
        // CREATE COUPON
        // ─────────────────────────────────────────────────────────────────────
        
        Coupon coupon = new Coupon();
        coupon.setCode(code);
        coupon.setDiscountType(DiscountType.valueOf(req.getDiscountType().toUpperCase()));
        coupon.setDiscountValue(req.getDiscountValue());
        coupon.setMinOrderAmount(req.getMinOrderAmount());
        coupon.setValidFrom(parseDate(req.getValidFrom(), "validFrom"));
        coupon.setValidTo(parseDate(req.getValidTo(), "validTo"));
        coupon.setShopId(req.getShopId());
        coupon.setActive(true);
        
        Long id = couponRepository.save(coupon);
        coupon.setId(id);
        
        log.info("Coupon created successfully with ID: {}", id);
        return mapToResponse(coupon);
    }

    @Override
    @Transactional
    public CouponResponse updateCoupon(Long id, CouponRequest req) {
        log.info("Updating coupon with ID: {}", id);
        
        // Edge Case: Null ID
        if (id == null || id <= 0) {
            log.error("Invalid coupon ID: {}", id);
            throw new BadRequestException("Valid coupon ID is required");
        }
        
        validateCouponRequest(req);
        
        // Edge Case: Coupon not found
        Coupon existing = couponRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Coupon not found with ID: {}", id);
                    return new ResourceNotFoundException("Coupon not found with ID: " + id);
                });
        
        // Edge Case: Code changed to an existing code
        String newCode = req.getCode().toUpperCase().trim();
        if (!existing.getCode().equals(newCode)) {
            if (couponRepository.findByCode(newCode).isPresent()) {
                log.error("Coupon code already exists: {}", newCode);
                throw new BadRequestException("Coupon code '" + newCode + "' already exists");
            }
        }

        existing.setCode(newCode);
        existing.setDiscountType(DiscountType.valueOf(req.getDiscountType().toUpperCase()));
        existing.setDiscountValue(req.getDiscountValue());
        existing.setMinOrderAmount(req.getMinOrderAmount());
        existing.setValidFrom(parseDate(req.getValidFrom(), "validFrom"));
        existing.setValidTo(parseDate(req.getValidTo(), "validTo"));
        existing.setShopId(req.getShopId());

        couponRepository.update(existing);
        
        log.info("Coupon updated successfully: {}", id);
        return mapToResponse(existing);
    }

    @Override
    @Transactional
    public boolean deleteCoupon(Long id) {
        log.info("Deleting (soft) coupon with ID: {}", id);
        
        // Edge Case: Null ID
        if (id == null || id <= 0) {
            log.error("Invalid coupon ID: {}", id);
            throw new BadRequestException("Valid coupon ID is required");
        }
        
        // Edge Case: Coupon not found
        if (couponRepository.findById(id).isEmpty()) {
            log.error("Coupon not found with ID: {}", id);
            throw new ResourceNotFoundException("Coupon not found with ID: " + id);
        }
        
        boolean deleted = couponRepository.softDelete(id);
        log.info("Coupon {} soft deleted: {}", id, deleted);
        return deleted;
    }

    @Override
    public CouponResponse getCouponById(Long id) {
        log.info("Fetching coupon by ID: {}", id);
        
        // Edge Case: Null ID
        if (id == null || id <= 0) {
            log.error("Invalid coupon ID: {}", id);
            throw new BadRequestException("Valid coupon ID is required");
        }
        
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Coupon not found with ID: {}", id);
                    return new ResourceNotFoundException("Coupon not found with ID: " + id);
                });
        
        return mapToResponse(coupon);
    }

    @Override
    public List<CouponResponse> getCouponsByShop(Long shopId) {
        log.info("Fetching coupons for shop: {}", shopId);
        
        // Edge Case: Null shop ID
        if (shopId == null || shopId <= 0) {
            log.error("Invalid shop ID: {}", shopId);
            throw new BadRequestException("Valid shop ID is required");
        }
        
        List<Coupon> coupons = couponRepository.findByShopId(shopId);
        
        if (coupons == null) {
            coupons = Collections.emptyList();
        }
        
        log.info("Found {} coupons for shop: {}", coupons.size(), shopId);
        return coupons.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CouponResponse> getAllCoupons() {
        log.info("Fetching all coupons");
        
        List<Coupon> coupons = couponRepository.findAll();
        
        if (coupons == null) {
            coupons = Collections.emptyList();
        }
        
        log.info("Found {} total coupons", coupons.size());
        return coupons.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    @Override
    public ApiResponse<Coupon> validateCoupon(String code, Long userId, Double cartTotal) {
        log.info("Validating coupon - Code: {}, User: {}, CartTotal: {}", code, userId, cartTotal);
        
        // ─────────────────────────────────────────────────────────────────────
        // INPUT VALIDATION
        // ─────────────────────────────────────────────────────────────────────
        
        // Edge Case 1: Null or empty code
        if (code == null || code.trim().isEmpty()) {
            log.error("Coupon code is empty");
            throw new BadRequestException("Coupon code is required");
        }
        
        // Edge Case 2: Null user ID
        if (userId == null || userId <= 0) {
            log.error("Invalid user ID: {}", userId);
            throw new BadRequestException("Valid user ID is required");
        }
        
        // Edge Case 3: Null or invalid cart total
        if (cartTotal == null) {
            log.error("Cart total is null");
            throw new BadRequestException("Cart total is required");
        }
        
        if (cartTotal <= 0) {
            log.error("Invalid cart total: {}", cartTotal);
            throw new BadRequestException("Cart total must be greater than zero");
        }
        
        // ─────────────────────────────────────────────────────────────────────
        // COUPON LOOKUP
        // ─────────────────────────────────────────────────────────────────────
        
        String normalizedCode = code.toUpperCase().trim();
        
        // Edge Case 4: Coupon not found
        Coupon coupon = couponRepository.findByCode(normalizedCode)
                .orElseThrow(() -> {
                    log.error("Coupon not found: {}", normalizedCode);
                    return new ResourceNotFoundException("Invalid coupon code: " + normalizedCode);
                });
        
        // ─────────────────────────────────────────────────────────────────────
        // COUPON STATUS VALIDATION
        // ─────────────────────────────────────────────────────────────────────
        
        LocalDate today = LocalDate.now();
        
        // Edge Case 5: Coupon is inactive
        if (!coupon.isActive()) {
            log.error("Coupon {} is inactive", normalizedCode);
            throw new BadRequestException("This coupon is no longer active");
        }
        
        // Edge Case 6: Coupon not yet valid
        if (coupon.getValidFrom() != null && coupon.getValidFrom().isAfter(today)) {
            log.error("Coupon {} not yet valid. Valid from: {}", normalizedCode, coupon.getValidFrom());
            throw new BadRequestException("This coupon is not yet active. Valid from: " + coupon.getValidFrom());
        }
        
        // Edge Case 7: Coupon expired
        if (coupon.getValidTo() != null && coupon.getValidTo().isBefore(today)) {
            log.error("Coupon {} has expired on: {}", normalizedCode, coupon.getValidTo());
            throw new BadRequestException("This coupon has expired on: " + coupon.getValidTo());
        }
        
        // ─────────────────────────────────────────────────────────────────────
        // USAGE VALIDATION
        // ─────────────────────────────────────────────────────────────────────
        
        // Edge Case 8: User already used this coupon
        if (couponRepository.isUsedByUser(userId, coupon.getId())) {
            log.error("User {} has already used coupon {}", userId, normalizedCode);
            throw new BadRequestException("You have already used this coupon");
        }
        
        // ─────────────────────────────────────────────────────────────────────
        // AMOUNT VALIDATION
        // ─────────────────────────────────────────────────────────────────────
        
        BigDecimal total = new BigDecimal(cartTotal);
        
        // Edge Case 9: Minimum order amount not met
        if (coupon.getMinOrderAmount() != null && total.compareTo(coupon.getMinOrderAmount()) < 0) {
            log.error("Cart total {} is less than minimum order amount {}", total, coupon.getMinOrderAmount());
            throw new BadRequestException("Minimum order amount of ₹" + coupon.getMinOrderAmount() + 
                    " is required. Your cart total is ₹" + total);
        }
        
        // ─────────────────────────────────────────────────────────────────────
        // CALCULATE DISCOUNT
        // ─────────────────────────────────────────────────────────────────────
        
        BigDecimal discountAmount = calculateDiscount(coupon, total);
        
        String message = String.format("Coupon applied successfully! You save ₹%.2f", discountAmount);
        log.info("Coupon {} validated successfully. Discount: ₹{}", normalizedCode, discountAmount);
        
        return new ApiResponse<>(true, message, coupon);
    }

    // ════════════════════════════════════════════════════════════════════════
    // PRIVATE HELPER METHODS
    // ════════════════════════════════════════════════════════════════════════

    private void validateCouponRequest(CouponRequest req) {
        // Edge Case: Null request
        if (req == null) {
            log.error("Coupon request is null");
            throw new BadRequestException("Coupon request data is required");
        }
        
        // Edge Case: Null or empty code
        if (req.getCode() == null || req.getCode().trim().isEmpty()) {
            log.error("Coupon code is empty");
            throw new BadRequestException("Coupon code is required");
        }
        
        // Edge Case: Code too short
        if (req.getCode().trim().length() < 3) {
            log.error("Coupon code too short: {}", req.getCode());
            throw new BadRequestException("Coupon code must be at least 3 characters");
        }
        
        // Edge Case: Code too long
        if (req.getCode().length() > 20) {
            log.error("Coupon code too long: {}", req.getCode().length());
            throw new BadRequestException("Coupon code cannot exceed 20 characters");
        }
        
        // Edge Case: Invalid discount type
        if (req.getDiscountType() == null || req.getDiscountType().trim().isEmpty()) {
            log.error("Discount type is empty");
            throw new BadRequestException("Discount type is required (PERCENT or FLAT)");
        }
        
        try {
            DiscountType.valueOf(req.getDiscountType().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid discount type: {}", req.getDiscountType());
            throw new BadRequestException("Invalid discount type: " + req.getDiscountType() + ". Must be PERCENT or FLAT");
        }
        
        // Edge Case: Null discount value
        if (req.getDiscountValue() == null) {
            log.error("Discount value is null");
            throw new BadRequestException("Discount value is required");
        }
        
        // Edge Case: Invalid discount value
        if (req.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid discount value: {}", req.getDiscountValue());
            throw new BadRequestException("Discount value must be greater than zero");
        }
        
        // Edge Case: Percentage discount > 100%
        if ("PERCENT".equalsIgnoreCase(req.getDiscountType()) && 
            req.getDiscountValue().compareTo(new BigDecimal("100")) > 0) {
            log.error("Percentage discount exceeds 100%: {}", req.getDiscountValue());
            throw new BadRequestException("Percentage discount cannot exceed 100%");
        }
        
        // Edge Case: Null min order amount
        if (req.getMinOrderAmount() == null) {
            log.error("Minimum order amount is null");
            throw new BadRequestException("Minimum order amount is required");
        }
        
        // Edge Case: Negative min order amount
        if (req.getMinOrderAmount().compareTo(BigDecimal.ZERO) < 0) {
            log.error("Negative minimum order amount: {}", req.getMinOrderAmount());
            throw new BadRequestException("Minimum order amount cannot be negative");
        }
        
        // Edge Case: Null dates
        if (req.getValidFrom() == null || req.getValidFrom().trim().isEmpty()) {
            log.error("Valid from date is empty");
            throw new BadRequestException("Valid from date is required (format: YYYY-MM-DD)");
        }
        
        if (req.getValidTo() == null || req.getValidTo().trim().isEmpty()) {
            log.error("Valid to date is empty");
            throw new BadRequestException("Valid to date is required (format: YYYY-MM-DD)");
        }
        
        // Parse and validate dates
        LocalDate validFrom = parseDate(req.getValidFrom(), "validFrom");
        LocalDate validTo = parseDate(req.getValidTo(), "validTo");
        
        // Edge Case: validTo before validFrom
        if (validTo.isBefore(validFrom)) {
            log.error("validTo {} is before validFrom {}", validTo, validFrom);
            throw new BadRequestException("Valid To date cannot be before Valid From date");
        }
        
        // Edge Case: Shop ID validation (if provided)
        if (req.getShopId() != null) {
            validateShopExists(req.getShopId());
        }
    }
    
    /**
     * Validate that the shop exists in the database
     */
    private void validateShopExists(Long shopId) {
        if (shopId <= 0) {
            log.error("Invalid shop ID: {}", shopId);
            throw new BadRequestException("Invalid shop ID: " + shopId);
        }
        
        // Check if shop exists in database using ShopRepository
        if (shopRepository.getShopById(shopId).isEmpty()) {
            log.error("Shop not found with ID: {}", shopId);
            throw new ResourceNotFoundException("Shop not found with ID: " + shopId);
        }
        log.debug("Shop {} validated successfully", shopId);
    }

    private LocalDate parseDate(String dateStr, String fieldName) {
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            log.error("Invalid date format for {}: {}", fieldName, dateStr);
            throw new BadRequestException("Invalid date format for " + fieldName + ": " + dateStr + ". Expected format: YYYY-MM-DD");
        }
    }

    private BigDecimal calculateDiscount(Coupon coupon, BigDecimal cartTotal) {
        if (coupon.getDiscountType() == DiscountType.PERCENT) {
            // Percentage discount
            BigDecimal percentage = coupon.getDiscountValue().divide(new BigDecimal("100"));
            return cartTotal.multiply(percentage);
        } else {
            // Flat discount
            return coupon.getDiscountValue().min(cartTotal); // Don't exceed cart total
        }
    }

    private CouponResponse mapToResponse(Coupon c) {
        CouponResponse r = new CouponResponse();
        r.setId(c.getId());
        r.setCode(c.getCode());
        r.setDiscountType(c.getDiscountType().name());
        r.setDiscountValue(c.getDiscountValue() != null ? c.getDiscountValue().doubleValue() : 0.0);
        r.setMinOrderAmount(c.getMinOrderAmount() != null ? c.getMinOrderAmount().doubleValue() : 0.0);
        r.setValidFrom(c.getValidFrom());
        r.setValidTo(c.getValidTo());
        r.setActive(c.isActive());
        r.setShopId(c.getShopId());
        return r;
    }
}