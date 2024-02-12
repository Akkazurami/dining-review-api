package com.example.DiningReview.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.DiningReview.model.Restaurant;

public interface RestaurantRepository extends CrudRepository<Restaurant, Integer> {
    Optional<Restaurant> findByNameAndZipCode(String name, String zipCode);
    List<Restaurant> findByZipCodeOrderByReviewScoreDesc(String zipCode);
    List<Restaurant> findByZipCodeAndPeanutScoreIsNotNullOrderByPeanutScoreDesc(String zipCode);
    List<Restaurant> findByZipCodeAndEggScoreIsNotNullOrderByEggScoreDesc(String zipCode);
    List<Restaurant> findByZipCodeAndDairyScoreIsNotNullOrderByDairyScoreDesc(String zipCode);
}
