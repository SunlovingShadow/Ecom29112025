package com.ecommerce.dto;

/**
 * DTO for creating and updating user data.
 * This comes from client-side (Postman / frontend).
 */
public class UserRequest {

    private String username;      // required
    private String email;         // required
    private String password;      // required only for create
    private String fullName;      
    private String phone;

    // CUSTOMER / SHOPKEEPER / ADMIN
    private String role;

    // ACTIVE / SUSPENDED / PENDING (optional during update)
    private String accountStatus;

    // ---------- Getters & Setters ----------

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

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

    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }

    public String getAccountStatus() {
        return accountStatus;
    }
    
    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }
}