package com.ecommerce.repository;

import com.ecommerce.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    // ---- CREATE ----
    Long save(User user);    // return generated ID

    // ---- UPDATE ----
    boolean update(User user);
    
    // Soft delete (by changing account_status = SUSPENDED)
    boolean softDelete(Long id);
    
    // Update only status
    boolean updateAccountStatus(Long id, String status);

    // ---- READ ----
    Optional<User> findById(Long id);

    // User must be ACTIVE or PENDING (not suspended)
    Optional<User> findActiveByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findAllActive();   // all ACTIVE users

    List<User> findAll();         // including suspended (admin use)

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}