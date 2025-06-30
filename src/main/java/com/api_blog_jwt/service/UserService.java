package com.api_blog_jwt.service;

import com.api_blog_jwt.entity.Role;
import com.api_blog_jwt.entity.User;
import com.api_blog_jwt.repository.RoleRepository;
import com.api_blog_jwt.repository.UserRepository;
import com.api_blog_jwt.security.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository ;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }


    public User RegisterUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Set<Role> roles = new HashSet<>();

        // Si des rôles sont passés dans le JSON
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            for (Role role : user.getRoles()) {
                Role existingRole = roleRepository.findByName(role.getName())
                        .orElseThrow(() -> new RuntimeException("Role not found: " + role.getName()));
                roles.add(existingRole);
            }
        } else {
            // Sinon on met le rôle USER par défaut
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            roles.add(userRole);
        }

        user.setRoles(roles);

        return userRepository.save(user);
    }


    public String authenticateUser(String email, String password) {
        // 1. Vérifie si l'utilisateur existe
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // 2. Vérifie le mot de passe
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Le mot de passe incorrecte");
        }

        // 3. Génère le token JWT
        // Tu dois avoir cette méthode dans JwtUtil

        // 4. Retourne le token
        return jwtUtil.generateToken(user);
    }

    public User getCurrentUser() {
        // 1. Récupère l'utilisateur courant depuis le contexte de sécurité
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Trouve l'utilisateur par email
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
    public List<User> getAllUsers() {

        return userRepository.findAll();

    }

    public User updateUser(User newUserData){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User existUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Mettre à jour les champs nécessaires

        if(newUserData.getPassword() != null&& !newUserData.getPassword().isEmpty()) {

            existUser.setPassword(passwordEncoder.encode(newUserData.getPassword()));
        }

        return userRepository.save(existUser);
    }

    public void deleteUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        userRepository.delete(user);
    }

}
