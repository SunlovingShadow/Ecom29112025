package com.ecommerce.model;

import java.time.LocalDateTime;
import com.ecommerce.enums.UserRole;
import com.ecommerce.enums.AccountStatus;

public class User {

    private Long id;                     // Primary key
    private String username;             // Unique login username
    private String email;                // User's email
    private String passwordHash;         // Encrypted password
    private UserRole role;               // CUSTOMER / SHOPKEEPER / ADMIN
    private String fullName;             // Full name of user
    private String phone;                // Mobile number
    private AccountStatus accountStatus; // ACTIVE / SUSPENDED / PENDING
    private LocalDateTime createdAt;     // Creation timestamp

    // ----- Getters -----
    public Long getId() {
        return id;
    }

    // ----- Setters -----
    public void setId(Long id) {
        this.id = id;
    }

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

    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
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

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }
    
    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}