package com.api_blog_jwt.service;

import com.api_blog_jwt.entity.Post;
import com.api_blog_jwt.entity.User;
import com.api_blog_jwt.repository.PostRepository;
import com.api_blog_jwt.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder; // Si besoin pour sécuriser des updates éventuelles (optionnel)

    public PostService(PostRepository postRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 1. Récupérer tous les posts
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // 2. Récupérer un post par ID
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    // 3. Créer un post pour l'utilisateur connecté
    public Post createPost(Post post, UserDetails userDetails) {
        User author = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        post.setAuthor(author);

        return postRepository.save(post);
    }

    // 4. Mettre à jour un post (si admin ou auteur)
    public Post updatePost(Post postData, Long postId, UserDetails userDetails) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAuthor = existingPost.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

        if (!isAuthor && !isAdmin) {
            throw new RuntimeException("Not authorized to update this post");
        }

        // Mise à jour des champs (tu adaptes selon ce que t'as dans ton entité)
        existingPost.setTitle(postData.getTitle());
        existingPost.setContent(postData.getContent());
        existingPost.setTitle(postData.getTitle());

        return postRepository.save(existingPost);
    }

    // 5. Supprimer un post (si admin ou auteur)
    public void deletePost(Long postId, UserDetails userDetails) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAuthor = post.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

        if (!isAuthor && !isAdmin) {
            throw new RuntimeException("Not authorized to delete this post");
        }

        postRepository.delete(post);
    }
}
