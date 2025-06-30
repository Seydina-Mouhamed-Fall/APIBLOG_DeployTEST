package com.api_blog_jwt.repository;

import com.api_blog_jwt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository  extends JpaRepository<User, Long> {

    // Method to find a user by email
    Optional<User> findByEmail(String email);

    // Method to check if a user exists by email
    boolean existsByEmail(String email);

    // Method to find a user by ID
    Optional<User> findById(Long id);

    // Method to delete a user by ID
    void deleteById(Long id);
}
