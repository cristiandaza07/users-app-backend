package com.springboot.backend.address.userapp.users_backend.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.address.userapp.users_backend.auth.JwtUtil;
import com.springboot.backend.address.userapp.users_backend.entities.Post;
import com.springboot.backend.address.userapp.users_backend.entities.User;
import com.springboot.backend.address.userapp.users_backend.models.FindAllPostsDTO;
import com.springboot.backend.address.userapp.users_backend.models.PostRequestDTO;
import com.springboot.backend.address.userapp.users_backend.models.UserFindAllPostsDTO;
import com.springboot.backend.address.userapp.users_backend.repositories.PostRepository;
import com.springboot.backend.address.userapp.users_backend.repositories.UserRepository;

@Service
public class PostServiceImpl implements PostService {
    
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;

    }

    @Override
    @Transactional(readOnly = true)
    public List<FindAllPostsDTO> findAll() {
        List<Post> posts = (List<Post>) postRepository.findAll();
        List<FindAllPostsDTO> postDTOs = new ArrayList<>();

        for (Post post : posts) {
            UserFindAllPostsDTO userDTO = new UserFindAllPostsDTO();
            userDTO.setId(post.getUser().getId());
            userDTO.setUserName(post.getUser().getUserName());
            userDTO.setName(post.getUser().getName());
            userDTO.setLastName(post.getUser().getLastName());
            userDTO.setEmail(post.getUser().getEmail());

            FindAllPostsDTO postDTO = new FindAllPostsDTO();
            postDTO.setId(post.getId());
            postDTO.setContent(post.getContent());
            postDTO.setTitle(post.getTitle());
            postDTO.setPostDate(post.getPostDate());
            postDTO.setUser(userDTO);

            postDTOs.add(postDTO);
        }

        return postDTOs;
    }


    @Transactional
    @Override
    public Post savePost(PostRequestDTO postDTO, String token) {
        
        String username = jwtUtil.getUsernameFromToken(token);

        Post post = new Post();
        post.setTitle(postDTO.getTitle()); 
        post.setContent(postDTO.getContent()); 

        Optional<User> userOptional = userRepository.findByUserName(username);

        if (userOptional.isPresent()) {
            post.setUser(userOptional.get());
            post.setPostDate(LocalDate.now());
        }
        
        return postRepository.save(post);

    }
    
}
