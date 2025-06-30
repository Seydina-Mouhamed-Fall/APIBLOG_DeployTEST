package com.api_blog_jwt.controller;


import com.api_blog_jwt.entity.User;
import com.api_blog_jwt.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Enregistrement (public)
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User savedUser = userService.RegisterUser(user);
//        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    //  Login (public)
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        String token = userService.authenticateUser(email, password);
        return ResponseEntity.ok(token);
    }

    //  Voir son profil (USER ou ADMIN)
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<User> getCurrentUser() {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }

    //  Modifier ses infos (USER ou ADMIN)
    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<User> updateUser(@RequestBody User newData) {
        User updatedUser = userService.updateUser(newData);
        return ResponseEntity.ok(updatedUser);
    }

    //  Supprimer son compte (USER ou ADMIN)
    @DeleteMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteUser() {
        userService.deleteUser();
        return ResponseEntity.noContent().build();
    }

    //  ADMIN ONLY : Liste de tous les utilisateurs
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers(); // méthode à créer dans le service
        return ResponseEntity.ok(users);
    }

}
