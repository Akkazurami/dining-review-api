package com.example.DiningReview.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.Iterable;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

import com.example.DiningReview.repository.RestaurantRepository;
import com.example.DiningReview.repository.ReviewRepository;
import com.example.DiningReview.repository.UserRepository;
import com.example.DiningReview.model.Restaurant;
import com.example.DiningReview.model.Review;
import com.example.DiningReview.model.ReviewStatus;
import com.example.DiningReview.model.User;

@RestController
@RequestMapping()
public class DiningReviewController {
    private RestaurantRepository restaurantRepository;
    private ReviewRepository reviewRepository;
    private UserRepository userRepository;

    public DiningReviewController(RestaurantRepository restaurantRepository, ReviewRepository reviewRepository, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/restaurants")
    public Restaurant createRestaurant(@RequestBody(required = false) Restaurant restaurant) {
        Optional<Restaurant> restaurantExistsOptional = this.restaurantRepository.findByNameAndZipCode(restaurant.getName(), restaurant.getZipCode());
        if (restaurantExistsOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Restaurant already exists");
        }
        Restaurant newRestaurant = this.restaurantRepository.save(restaurant);
        return newRestaurant;
    }

    @GetMapping("/restaurants")
    public Iterable<Restaurant> getRestaurants() {
        return this.restaurantRepository.findAll();
    }

    @GetMapping("/restaurants/{id}")
    public Optional<Restaurant> getRestaurantById(@PathVariable Integer id) {
        return this.restaurantRepository.findById(id);
    }

    @GetMapping("/restaurants/search")
    public List<Restaurant> searchRestaurants(@RequestParam String zipCode, @RequestParam(required = false) String allergy) {
        if (allergy == null) {
            return this.restaurantRepository.findByZipCodeOrderByReviewScoreDesc(zipCode);
        } else if (allergy.equals("peanut")) {
            return this.restaurantRepository.findByZipCodeAndPeanutScoreIsNotNullOrderByPeanutScoreDesc(zipCode);
        } else if (allergy.equals("egg")) {
            return this.restaurantRepository.findByZipCodeAndEggScoreIsNotNullOrderByEggScoreDesc(zipCode);
        } else if (allergy.equals("dairy")) {
            return this.restaurantRepository.findByZipCodeAndDairyScoreIsNotNullOrderByDairyScoreDesc(zipCode);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Acceptable allergy parameters are: peanut, egg, dairy.");
        }
    }

    @PostMapping("/reviews")
    public Review creatReview(@RequestBody(required = false) Review review) {
        Optional<User> userExists = this.userRepository.findByDisplayName(review.getDisplayName());
        if (!userExists.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist. Create user before submitting a review.");
        } else {
            review.setReviewStatus(ReviewStatus.Pending);
            Review newReview = this.reviewRepository.save(review);
            return newReview;
        }
    }

    @GetMapping("/reviews")
    public Iterable<Review> getReviews() {
        return this.reviewRepository.findAll();
    }

    @GetMapping("/reviews/byRestaurantId/{restaurantId}")
    public List<Review> getApprovedReviewsByRestaurantId(@PathVariable Integer restaurantId) {
        return this.reviewRepository.findByRestaurantIdAndReviewStatus(restaurantId, ReviewStatus.Approved);
    }

    @GetMapping("/reviews/{id}")
    public Optional<Review> getReviewById(@PathVariable Integer id) {
        return this.reviewRepository.findById(id);
    }

    @GetMapping("/admin/pending")
    public List<Review> getPendingReviews() {
        return this.reviewRepository.findByReviewStatus(ReviewStatus.Pending);
    }

    @PutMapping("/admin")
    public Restaurant updateReviewStatus(@RequestBody Review review) {
        // Get and update the review status
        Optional<Review> reviewOptional = this.reviewRepository.findById(review.getId());
        Review reviewToUpdate;
        if (reviewOptional.isPresent()) {
            reviewToUpdate = reviewOptional.get();
            reviewToUpdate.setReviewStatus(review.getReviewStatus());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Review does not exist.");
        }
        this.reviewRepository.save(reviewToUpdate);
        
        // Get approved reviews for restaurant and calculate new scores
        List<Review> approvedReviews = this.reviewRepository.findByRestaurantIdAndReviewStatus(review.getRestaurantId(), ReviewStatus.Approved);
        List<Double> peanutScores = new ArrayList<Double>();
        List<Double> eggScores = new ArrayList<Double>();
        List<Double> dairyScores = new ArrayList<Double>();
        for (Review approvedReview : approvedReviews) {
            if (approvedReview.getPeanutScore() != null) {
                peanutScores.add(approvedReview.getPeanutScore());
            }
            if (approvedReview.getEggScore() != null) {
                eggScores.add(approvedReview.getEggScore());
            }
            if (approvedReview.getDairyScore() != null) {
                dairyScores.add(approvedReview.getDairyScore());
            }
        }
        
        Double peanutSum = 0.0;
        for (Double score : peanutScores) {
            peanutSum += score;
        }
        Double newPeanutScore = peanutSum / peanutScores.size();

        Double eggSum = 0.0;
        for (Double score : eggScores) {
            eggSum += score;
        }
        Double newEggScore = eggSum / eggScores.size();

        Double dairySum = 0.0;
        for (Double score : dairyScores) {
            dairySum += score;
        }
        Double newDairyScore = dairySum / dairyScores.size();

        Double newReviewScore = (peanutSum + eggSum + dairySum) / (peanutScores.size() + eggScores.size() + dairyScores.size());

        //Get restaurant and update scores
        Restaurant restaurantToUpdate = this.restaurantRepository.findById(review.getRestaurantId()).get();
        restaurantToUpdate.setPeanutScore(newPeanutScore);
        restaurantToUpdate.setEggScore(newEggScore);
        restaurantToUpdate.setDairyScore(newDairyScore);
        restaurantToUpdate.setReviewScore(newReviewScore);

        Restaurant updatedRestaurant = this.restaurantRepository.save(restaurantToUpdate);
        System.out.println(peanutScores);
        System.out.println(peanutSum);
        return updatedRestaurant;
    }

    @PostMapping("/users")
    public User createUser(@RequestBody(required = false) User user) {
        Optional<User> userExistsOptional = this.userRepository.findByDisplayName(user.getDisplayName());
        if(userExistsOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists");
        }
        User newUser = this.userRepository.save(user);
        return newUser;
    }

    @GetMapping("/users")
    public Iterable<User> getUsers() {
        return this.userRepository.findAll();
    }

    @GetMapping("/users/{displayName}")
    public Optional<User> getUser(@PathVariable String displayName) {
        return this.userRepository.findByDisplayName(displayName);
    }

    @PutMapping("/users/{displayName}")
    public User updateUser(@RequestBody(required = false) User user, @PathVariable String displayName) {
        Optional<User> userToUpdateOptional = this.userRepository.findByDisplayName(displayName);
        if (!userToUpdateOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist.");
        }
        User userToUpdate = userToUpdateOptional.get();
        if (!user.getDisplayName().equals(userToUpdate.getDisplayName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Display names cannot be changed.");
        }

        if (user.getCity() != null) {
            userToUpdate.setCity(user.getCity());
        }

        if (user.getState() != null) {
            userToUpdate.setState(user.getState());
        }

        if (user.getZipCode() != null) {
            userToUpdate.setZipCode(user.getZipCode());
        }

        if (user.getPeanutAllergy() != null) {
            userToUpdate.setPeanutAllergy(user.getPeanutAllergy());
        }

        if (user.getEggAllergy() != null) {
            userToUpdate.setEggAllergy(user.getEggAllergy());
        }
        
        if (user.getDairyAllergy() != null) {
            userToUpdate.setDairyAllergy(user.getDairyAllergy());
        }
        User updatedUser = this.userRepository.save(userToUpdate);
        return updatedUser;
    }
}
