package com.ecommerce.controller;

import com.ecommerce.dto.UserRequest;
import com.ecommerce.dto.UserResponse;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * USER CONTROLLER
 * Handles CRUD operations for users.
 * NO HARD DELETE â€” Only soft delete allowed.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Constructor Injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ------------------------------------------------------------
    // CREATE USER
    // ------------------------------------------------------------
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        UserResponse response = userService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ------------------------------------------------------------
    // UPDATE USER
    // ------------------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequest request) {

        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    // ------------------------------------------------------------
    // GET USER BY ID
    // ------------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    // ------------------------------------------------------------
    // GET ALL ACTIVE USERS
    // ------------------------------------------------------------
    @GetMapping("/active")
    public ResponseEntity<List<UserResponse>> getAllActiveUsers() {
        List<UserResponse> list = userService.getAllActiveUsers();
        return ResponseEntity.ok(list);
    }

    // ------------------------------------------------------------
    // GET ALL USERS (ADMIN)
    // ------------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> list = userService.getAllUsers();
        return ResponseEntity.ok(list);
    }

    // ------------------------------------------------------------
    // SOFT DELETE USER
    // Changes account_status = SUSPENDED
    // ------------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<String> softDeleteUser(@PathVariable Long id) {
        boolean deleted = userService.softDeleteUser(id);

        if (!deleted) {
            throw new BadRequestException("Unable to delete user!");
        }

        return ResponseEntity.ok("User soft-deleted successfully (status = SUSPENDED)");
    }

    // ------------------------------------------------------------
    // UPDATE ACCOUNT STATUS (Admin action)
    // ACTIVE / SUSPENDED / PENDING
    // ------------------------------------------------------------
    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateAccountStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        boolean updated = userService.updateAccountStatus(id, status.toUpperCase());

        if (!updated) {
            throw new BadRequestException("Failed to update user status");
        }

        return ResponseEntity.ok("Account status updated successfully");
    }
}