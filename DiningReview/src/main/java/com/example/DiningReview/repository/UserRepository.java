package com.example.DiningReview.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.DiningReview.model.User;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByDisplayName(String displayName);
    
}
