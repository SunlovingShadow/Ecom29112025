package com.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import com.ecommerce.model.Review;

public interface ReviewRepository {
	
	void save(Review review);
	Optional<Review> findById(Long id);
	List<Review> findByNewest(Long productId);
	List<Review> findByRatingHigh(Long productId);
	List<Review> findByRatingLow(Long productId);
	void delete(Long reviewId,Long userId);
	Double avgRating(Long productId);

}