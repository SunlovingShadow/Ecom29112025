package com.ecommerce.repository;

import com.ecommerce.model.Address;

import java.util.List;
import java.util.Optional;

public interface AddressRepository {

    // -------- CREATE --------
    Long save(Address address);

    // -------- UPDATE --------
    boolean update(Address address);

    // -------- HARD DELETE --------
    boolean deleteById(Long id, Long userId);

    // -------- FIND BY ID WITH USER VALIDATION --------
    Optional<Address> findByIdAndUser(Long id, Long userId);

    // -------- LIST OF USER'S ADDRESSES --------
    List<Address> findAllByUser(Long userId);

    // -------- DEFAULT ADDRESS OPERATIONS --------

    // Unset all defaults for user
    boolean unsetAllDefaults(Long userId);

    // Set one default address for user
    boolean setDefault(Long userId, Long addressId);

    // Check if address belongs to user
    boolean existsByIdAndUser(Long id, Long userId);

    // Count addresses for user (optional use)
    int countByUser(Long userId);
}