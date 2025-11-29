package com.ecommerce.service;

import com.ecommerce.dto.ReturnRequestDTO;
import com.ecommerce.model.ReturnRequest;

import java.util.List;

public interface ReturnService {
	// ... existing ...
	List<ReturnRequest> getUserReturnRequests(Long userId);
    void requestReturn(Long userId, ReturnRequestDTO requestDto);
    ReturnRequest getReturnByOrderId(Long userId, Long orderId);
}