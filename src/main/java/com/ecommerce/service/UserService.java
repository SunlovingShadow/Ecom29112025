package com.ecommerce.service;

import com.ecommerce.model.User;
import com.ecommerce.dto.UserRequest;
import com.ecommerce.dto.UserResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {

    // CREATE USER
    UserResponse createUser(UserRequest request);

    // UPDATE USER
    UserResponse updateUser(Long id, UserRequest request);

    // GET USER BY ID
    UserResponse getUserById(Long id);

    // GET ACTIVE USER BY USERNAME (used in login)
    Optional<User> getActiveUserByUsername(String username);

    // GET ALL ACTIVE USERS
    List<UserResponse> getAllActiveUsers();

    // GET ALL USERS (ADMIN)
    List<UserResponse> getAllUsers();

    // SOFT DELETE USER (account_status = SUSPENDED)
    boolean softDeleteUser(Long id);

    // UPDATE ACCOUNT STATUS (Admin)
    boolean updateAccountStatus(Long id, String status);
}