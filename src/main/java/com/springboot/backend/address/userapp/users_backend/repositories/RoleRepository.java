package com.springboot.backend.address.userapp.users_backend.repositories;

import org.springframework.data.repository.CrudRepository;

import com.springboot.backend.address.userapp.users_backend.entities.Role;
import java.util.List;
import java.util.Optional;


public interface RoleRepository extends CrudRepository<Role, Long> {
    
    Optional<Role> findByName(String name);
}
