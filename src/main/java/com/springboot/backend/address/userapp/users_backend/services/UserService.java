package com.springboot.backend.address.userapp.users_backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.springboot.backend.address.userapp.users_backend.entities.User;
import com.springboot.backend.address.userapp.users_backend.models.UserRequestDTO;

public interface UserService {
    List<User> findAll();

    Optional<User> findById(Long id);

    User save(User user);

    Optional<User> update(UserRequestDTO user, Long id);

    void deleteById(Long id);

    Page<User> findAll(Pageable pageable);


}
