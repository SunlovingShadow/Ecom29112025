package com.ecommerce.repository;

import com.ecommerce.model.Payment;

public interface PaymentRepository {
    Payment save(Payment payment);
}