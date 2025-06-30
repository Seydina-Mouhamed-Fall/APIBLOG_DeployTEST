package com.api_blog_jwt;

import com.api_blog_jwt.entity.Role;
import com.api_blog_jwt.entity.User;
import com.api_blog_jwt.repository.RoleRepository;
import com.api_blog_jwt.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ApiBlogJwtApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiBlogJwtApplication.class, args);
    }


    @Bean
    public CommandLineRunner initData(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Initialize roles
          if(roleRepository.findByName("ROLE_USER").isEmpty()) {
                Role userRole = new Role();
                userRole.setName("ROLE_USER");
                roleRepository.save(userRole);
                System.out.println("Rôle ROLE_USER créé avec succès.");
            } else {
                System.out.println("Rôle ROLE_USER déjà existant.");
            }

            // Create the ROLE_ADMIN if it does not exist
            roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> {
                        Role adminRole = new Role();
                        adminRole.setName("ROLE_ADMIN");
                        return roleRepository.save(adminRole);
                    });
          };
        };
    }


