package com.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ecommerce.model.Review;
import com.ecommerce.repository.rowmapper.ReviewRowMapper;

@Repository
public class ReviewRepositoryImpl implements ReviewRepository {

	@Autowired
	private  JdbcTemplate jdbc;
	@Override
	public void save(Review review) {
		 String sql = """
	                INSERT INTO reviews(product_id, user_id, rating, title, body)
	                VALUES (?, ?, ?, ?, ?)
	                """;
	        jdbc.update(sql,
	                review.getProductId(),
	                review.getUserId(),
	                review.getRating(),
	                review.getTitle(),
	                review.getBody()
	        );
		
	}
	
	@Override
	public Optional<Review> findById(Long id) {
		String sql="SELECT * FROM reviews WHERE id=?";
		List<Review> list=jdbc.query(sql, new ReviewRowMapper(),id);
		return list.stream().findFirst();
	}

	@Override
	public List<Review> findByNewest(Long productId) {
		
		return jdbc.query(
                "SELECT * FROM reviews WHERE product_id=? AND is_deleted=0 ORDER BY created_at DESC",
                new ReviewRowMapper(),
                productId
        );
	}

	@Override
	public List<Review> findByRatingHigh(Long productId) {
		  return jdbc.query(
	                "SELECT * FROM reviews WHERE product_id=? AND is_deleted=0 ORDER BY rating DESC",
	                new ReviewRowMapper(),
	                productId
	        );

	}

	@Override
	public List<Review> findByRatingLow(Long productId) {
		 return jdbc.query(
	                "SELECT * FROM reviews WHERE product_id=? AND is_deleted=0 ORDER BY rating ASC",
	                new ReviewRowMapper(),
	                productId
	        );
	}

	@Override
	public void delete(Long reviewId, Long userId) {
		
		 jdbc.update("UPDATE reviews SET is_deleted=1 WHERE id=? AND user_id=?", reviewId, userId);
		
	}

	@Override
	public Double avgRating(Long productId) {
		
		String sql="""
                SELECT AVG(rating)
                 FROM reviews 
                 WHERE product_id=? AND is_deleted=0
                 """;
		Double avg=jdbc.queryForObject(sql, Double.class,productId);
                return avg!=null?avg:0.0;
	}

}