package com.ecommerce.repository;

import com.ecommerce.model.ReturnRequest;
import java.util.List;
import java.util.Optional;

public interface ReturnRepository {
    void save(ReturnRequest returnRequest);
    List<ReturnRequest> findByUserId(Long userId); // Join with orders to check user
    boolean existsByOrderId(Long orderId);
 // Add this line to your existing ReturnRepository interface
    Optional<ReturnRequest> findByOrderId(Long orderId);
}