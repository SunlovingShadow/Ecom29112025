package com.ecommerce.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.dto.ReviewRequest;
import com.ecommerce.dto.ReviewResponse;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.Review;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ReviewRepository;
import com.ecommerce.service.ReviewService;

@Service
public class ReviewServiceImpl implements ReviewService{

	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Override
	public ReviewResponse addReview(Long userId, ReviewRequest request) {
		 
		if(request.getRating()<1 || request.getRating()>5) {
			throw new BadRequestException("Rating must be between 1 and 5");
		}
		
		productRepository.findById(request.getProductId())
				.orElseThrow(()->new ResourceNotFoundException("Product not found with ID= "+request.getProductId()));	
		
		Review review = new Review();
	        review.setProductId(request.getProductId());
	        review.setUserId(userId);
	        review.setRating(request.getRating());
	        review.setTitle(request.getTitle());
	        review.setBody(request.getBody());

	        reviewRepository.save(review);

	        ReviewResponse resp = new ReviewResponse();
	        resp.setRating(review.getRating());
	        resp.setTitle(review.getTitle());
	        resp.setBody(review.getBody());
	        return resp;
		
	}
	
	@Override
	public List<ReviewResponse> getReviewById(Long reviewId){
		
		Optional <Review> reviewOpt=reviewRepository.findById(reviewId);
		if(reviewOpt.isEmpty()) {
			return List.of();
		}
		
		Review review=reviewOpt.get();
		ReviewResponse response=new ReviewResponse(
				review.getId(),
				review.getProductId(),
				review.getUserId(),
				review.getRating(),
				review.getTitle(),
				review.getBody(),
				review.getCreatedAt().toString());
		return List.of(response);
	}

	@Override
	public List<ReviewResponse> getNewestReviews(Long productId) {
		return reviewRepository.findByNewest(productId)
				.stream()
				.map(review->new ReviewResponse(
						review.getId(),
						review.getProductId(),
						review.getUserId(),
						review.getRating(),
						review.getTitle(),
						review.getBody(),
						review.getCreatedAt().toString()
						))
				.toList();

	}

	@Override
	public List<ReviewResponse> getHighestRatedReviews(Long productId) {
		return reviewRepository.findByRatingHigh(productId)
				.stream()
				.map(review->new ReviewResponse(
						review.getId(),
						review.getProductId(),
						review.getUserId(),
						review.getRating(),
						review.getTitle(),
						review.getBody(),
						review.getCreatedAt().toString()
						))
				.toList();
	}

	@Override
	public List<ReviewResponse> getLowestRatedReviews(Long productId) {
		return reviewRepository.findByRatingLow(productId)
				.stream()
				.map(review->new ReviewResponse(
						review.getId(),
						review.getProductId(),
						review.getUserId(),
						review.getRating(),
						review.getTitle(),
						review.getBody(),
						review.getCreatedAt().toString()
						))
				.toList();
	}

	@Override
	public void deleteReview(Long reviewId, Long userId) {
		reviewRepository.delete(reviewId, userId);
		
	}

	@Override
	public Double getAverageRating(Long productId) {
		return reviewRepository.avgRating(productId);
	}


}