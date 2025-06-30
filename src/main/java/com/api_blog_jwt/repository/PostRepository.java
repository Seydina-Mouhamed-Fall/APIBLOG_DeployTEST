package com.api_blog_jwt.repository;

import com.api_blog_jwt.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    // Method to find a post by title
    Optional<Post> findByTitle(String title);

    List<Post> findByAuthorId(Long authorId);
    // Method to check if a post exists by title
    boolean existsByTitle(String title);

    // Method to find a post by ID
    Optional<Post> findById(Long id);

    // Method to delete a post by ID
    void deleteById(Long id);
}
