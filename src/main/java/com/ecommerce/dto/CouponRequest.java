
package com.ecommerce.dto;

import java.math.BigDecimal;

public class CouponRequest {

    private String code;
    private String discountType; // "FLAT" or "PERCENT"
    
    // CHANGED FROM double TO BigDecimal (Required for Order Logic)
    private BigDecimal discountValue;
    private BigDecimal minOrderAmount;
    
    // KEEP AS STRING (Required for Snigdha's AdminService parsing logic)
    private String validFrom; 
    private String validTo;
    
    private Long shopId; // Nullable

    public CouponRequest() {
        super();
    }

    public CouponRequest(String code, String discountType, BigDecimal discountValue, BigDecimal minOrderAmount,
            String validFrom, String validTo, Long shopId) {
        super();
        this.code = code;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderAmount = minOrderAmount;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.shopId = shopId;
    }

    // --- GETTERS & SETTERS ---

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public BigDecimal getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(BigDecimal minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public String getValidTo() {
        return validTo;
    }

    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    @Override
    public String toString() {
        return "CouponRequest [code=" + code + ", discountType=" + discountType + ", discountValue=" + discountValue
                + ", minOrderAmount=" + minOrderAmount + ", validFrom=" + validFrom + ", validTo=" + validTo
                + ", shopId=" + shopId + "]";
    }
}