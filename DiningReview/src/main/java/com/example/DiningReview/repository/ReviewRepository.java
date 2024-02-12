package com.example.DiningReview.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.DiningReview.model.Review;
import com.example.DiningReview.model.ReviewStatus;

public interface ReviewRepository extends CrudRepository<Review, Integer> {
    List<Review> findByReviewStatus(ReviewStatus reviewStatus);
    List<Review> findByRestaurantIdAndReviewStatus(Integer restaurantId, ReviewStatus reviewStatus);
}
