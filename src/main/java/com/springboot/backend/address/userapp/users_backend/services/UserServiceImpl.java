package com.springboot.backend.address.userapp.users_backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.address.userapp.users_backend.entities.User;
import com.springboot.backend.address.userapp.users_backend.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired // Inyecta una instancia de UserRepository
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return (List) this.repository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return this.repository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }
    
    @Transactional
    @Override
    public User save(User userDTO) {
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setLastName(userDTO.getLastName());
        user.setName(userDTO.getName());
        user.setUserName(userDTO.getUserName());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        return repository.save(user);
    }
    
    @Transactional
    @Override
    public Optional<User> update(User user, Long id) {
        Optional<User> userOptional = repository.findById(id);

        if (userOptional.isPresent()) {
            User userDB = userOptional.get();
            userDB.setEmail(user.getEmail());
            userDB.setLastName(user.getLastName());
            userDB.setName(user.getName());
            userDB.setUserName(user.getUserName());

            return Optional.of(repository.save(userDB));
        }
        return Optional.empty();
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        repository.deleteById(id); 
    }



}
