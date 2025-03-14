package com.springboot.backend.address.userapp.users_backend.services;

import java.util.List;

import com.springboot.backend.address.userapp.users_backend.entities.Post;
import com.springboot.backend.address.userapp.users_backend.models.FindAllPostsDTO;
import com.springboot.backend.address.userapp.users_backend.models.PostRequestDTO;

public interface PostService {
    List<FindAllPostsDTO> findAll();

    Post savePost(PostRequestDTO post, String token);
}
