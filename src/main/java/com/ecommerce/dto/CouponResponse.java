package com.ecommerce.dto;

import java.time.LocalDate;
import java.util.Date;

import com.ecommerce.enums.DiscountType;

public class CouponResponse {

    private Long id;
    private String code;
    private String discountType;
    private double discountValue;
    private double minOrderAmount;
    private LocalDate validFrom;
    private LocalDate validTo;
    private boolean isActive;
    private Long shopId;
	
    public CouponResponse() {
		super();
	}

	public CouponResponse(Long id, String code, String discountType, double discountValue, double minOrderAmount,
			LocalDate validFrom, LocalDate validTo, boolean isActive, Long shopId) {
		super();
		this.id = id;
		this.code = code;
		this.discountType = discountType;
		this.discountValue = discountValue;
		this.minOrderAmount = minOrderAmount;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.isActive = isActive;
		this.shopId = shopId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public double getDiscountValue() {
		return discountValue;
	}

	public void setDiscountValue(double discountValue) {
		this.discountValue = discountValue;
	}

	public double getMinOrderAmount() {
		return minOrderAmount;
	}

	public void setMinOrderAmount(double minOrderAmount) {
		this.minOrderAmount = minOrderAmount;
	}

	

	public LocalDate getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(LocalDate validFrom) {
		this.validFrom = validFrom;
	}

	public LocalDate getValidTo() {
		return validTo;
	}

	public void setValidTo(LocalDate validTo) {
		this.validTo = validTo;
	}

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "CouponResponse [id=" + id + ", code=" + code + ", discountType=" + discountType + ", discountValue="
				+ discountValue + ", minOrderAmount=" + minOrderAmount + ", validFrom=" + validFrom + ", validTo="
				+ validTo + ", shopId=" + shopId + ", isActive=" + isActive + "]";
	}

	

}