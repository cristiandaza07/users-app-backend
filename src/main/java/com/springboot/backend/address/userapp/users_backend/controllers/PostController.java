package com.springboot.backend.address.userapp.users_backend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.backend.address.userapp.users_backend.entities.Post;
import com.springboot.backend.address.userapp.users_backend.entities.User;
import com.springboot.backend.address.userapp.users_backend.models.FindAllPostsDTO;
import com.springboot.backend.address.userapp.users_backend.models.PostRequestDTO;
import com.springboot.backend.address.userapp.users_backend.services.PostService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api/posts")
public class PostController {
    
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public List<FindAllPostsDTO> listPosts() {
        return postService.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createPost(@Valid @RequestBody PostRequestDTO post, @RequestHeader("Authorization") String authorizationHeader, BindingResult result) {
        if (result.hasErrors()) {
            return validation(result);
        }

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            authorizationHeader = authorizationHeader.substring(7);
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.savePost(post, authorizationHeader));
        
    }
    
    //Metodo para validaciones
    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> {
            errors.put(error.getField(), "El campo '" + error.getField() + "' "+ error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
    
}
