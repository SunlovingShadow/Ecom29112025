package com.ecommerce.service;

import java.util.List;

import com.ecommerce.dto.ReviewRequest;
import com.ecommerce.dto.ReviewResponse;

public interface ReviewService {

	ReviewResponse addReview(Long userId, ReviewRequest request);
	
	List<ReviewResponse> getReviewById(Long reviewId);

    List<ReviewResponse> getNewestReviews(Long productId);
    
    List<ReviewResponse> getHighestRatedReviews(Long productId);
    
    List<ReviewResponse> getLowestRatedReviews(Long productId); 

    void deleteReview(Long reviewId, Long userId);

    Double getAverageRating(Long productId);
}