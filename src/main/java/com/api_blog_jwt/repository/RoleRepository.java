package com.api_blog_jwt.repository;

import com.api_blog_jwt.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    // Method to find a role by name
    Optional<Role> findByName(String name);

    // Method to check if a role exists by name
    boolean existsByName(String name);
}
