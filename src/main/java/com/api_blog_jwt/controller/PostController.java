package com.api_blog_jwt.controller;

import com.api_blog_jwt.entity.Post;
import com.api_blog_jwt.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    //  Public : voir tous les posts
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    //  Public : voir un post par id
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    //  Créer un post (connecté)
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Post> createPost(
            @RequestBody Post post,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Post created = postService.createPost(post, userDetails);
        return ResponseEntity.ok(created);
    }

    //  Modifier un post (admin ou auteur)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')") // autorisation générale, le service filtre précisément
    public ResponseEntity<Post> updatePost(
            @PathVariable Long id,
            @RequestBody Post postData,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Post updated = postService.updatePost(postData, id, userDetails);
        return ResponseEntity.ok(updated);
    }

    // 🗑Supprimer un post (admin ou auteur)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        postService.deletePost(id, userDetails);
        return ResponseEntity.noContent().build();
    }

}
