package com.ecommerce.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.ecommerce.enums.DiscountType; // Make sure you have this Enum

public class Coupon {

    private Long id;
    private String code;
    private DiscountType discountType;   // PERCENT / FLAT
    
    // CHANGED FROM double TO BigDecimal (Crucial for your Order Logic)
    private BigDecimal discountValue;
    private BigDecimal minOrderAmount;
    
    private LocalDate validFrom;
    private LocalDate validTo;
    private Long shopId;                 // null = global coupon
    private boolean isActive;

    public Coupon() {
        super();
    }

    public Coupon(Long id, String code, DiscountType discountType, BigDecimal discountValue, BigDecimal minOrderAmount,
            LocalDate validFrom, LocalDate validTo, Long shopId, boolean isActive) {
        super();
        this.id = id;
        this.code = code;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderAmount = minOrderAmount;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.shopId = shopId;
        this.isActive = isActive;
    }

    // --- GETTERS AND SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public DiscountType getDiscountType() { return discountType; }
    public void setDiscountType(DiscountType discountType) { this.discountType = discountType; }

    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }

    public BigDecimal getMinOrderAmount() { return minOrderAmount; }
    public void setMinOrderAmount(BigDecimal minOrderAmount) { this.minOrderAmount = minOrderAmount; }

    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }

    public LocalDate getValidTo() { return validTo; }
    public void setValidTo(LocalDate validTo) { this.validTo = validTo; }

    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean isActive) { this.isActive = isActive; }

    @Override
    public String toString() {
        return "Coupon [id=" + id + ", code=" + code + ", discountType=" + discountType + ", discountValue="
                + discountValue + ", minOrderAmount=" + minOrderAmount + ", validFrom=" + validFrom + ", validTo="
                + validTo + ", shopId=" + shopId + ", isActive=" + isActive + "]";
    }
}