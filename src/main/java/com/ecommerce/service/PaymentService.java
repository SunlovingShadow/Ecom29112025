package com.ecommerce.service;

import com.ecommerce.dto.PaymentRequest;
import com.ecommerce.model.Payment;

public interface PaymentService {
    Payment processPayment(PaymentRequest request);
}