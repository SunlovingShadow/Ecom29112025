package com.ecommerce.dto;

/**
 * DTO for creating/updating a user's address.
 * Comes from client (Postman / Frontend).
 */
public class AddressRequest {

    private String fullName;
    private String phone;
    private String pincode;

    private String addressLine1;
    private String addressLine2;

    private String city;
    private String state;
    private String landmark;

    // HOME / WORK
    private String addressType;

    // Optional - if true, user wants to set this address as default
    private Boolean isDefault;

    // -------- Getters & Setters --------

    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPincode() {
        return pincode;
    }
    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getAddressLine1() {
        return addressLine1;
    }
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public String getLandmark() {
        return landmark;
    }
    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getAddressType() {
        return addressType;
    }
    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
}