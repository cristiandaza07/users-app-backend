package com.springboot.backend.address.userapp.users_backend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.address.userapp.users_backend.entities.Role;
import com.springboot.backend.address.userapp.users_backend.entities.User;
import com.springboot.backend.address.userapp.users_backend.models.IUser;
import com.springboot.backend.address.userapp.users_backend.models.UserRequestDTO;
import com.springboot.backend.address.userapp.users_backend.repositories.RoleRepository;
import com.springboot.backend.address.userapp.users_backend.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired // Inyecta una instancia de UserRepository
    private UserRepository userRepository;

    @Autowired // Inyecta una instancia de RoleRepository
    private RoleRepository roleRepository;

    @Autowired // Inyecta una instancia para el cifrado de la password
    private PasswordEncoder passwordEncoder;

    @Override 
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return ((List<User>) this.userRepository.findAll()).stream().map(user -> {
            boolean admin = user.getRoles().stream().anyMatch(role -> "ROLE_ADMIN".equals(role.getName()));
            user.setAdmin(admin);
            return user;
        }).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return this.userRepository.findAll(pageable).map(user -> {
            boolean admin = user.getRoles().stream().anyMatch(role -> "ROLE_ADMIN".equals(role.getName()));
            user.setAdmin(admin);
            return user;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
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
        user.setRoles(getRoles(userDTO));

        return userRepository.save(user);
    }

    
    @Transactional
    @Override
    public Optional<User> update(UserRequestDTO user, Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User userDB = userOptional.get();
            userDB.setEmail(user.getEmail());
            userDB.setLastName(user.getLastName());
            userDB.setName(user.getName());
            userDB.setUserName(user.getUserName());    
            userDB.setRoles(getRoles(user)); 

            return Optional.of(userRepository.save(userDB));
        }
        return Optional.empty();
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    private List<Role> getRoles(IUser user) {
        List<Role> roles = new ArrayList<>();
    
        //Aginación de rol 'USER'
        Optional<Role> optionalRoleUser = roleRepository.findByName("ROLE_USER");
        optionalRoleUser.ifPresent(role -> roles.add(role));
        
        if (user.isAdmin()) {
            // Aginación de rol 'ADMIN'
            Optional<Role> optionalRoleAdmin = roleRepository.findByName("ROLE_ADMIN");
            optionalRoleAdmin.ifPresent(role -> roles.add(role));
        }
        return roles;
    }
    

}
