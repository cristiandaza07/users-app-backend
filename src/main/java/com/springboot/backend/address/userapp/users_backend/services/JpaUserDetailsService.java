package com.springboot.backend.address.userapp.users_backend.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.address.userapp.users_backend.entities.User;
import com.springboot.backend.address.userapp.users_backend.repositories.UserRepository;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired // Inyecta una instancia de UserRepository
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busca un usuario por su nombre de usuario en la base de datos
        Optional<User> optionalUser = userRepository.findByUserName(username);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException(String.format("Username %n no existe en el sistema", username));
        }

        // Obtiene el usuario del Optional.
        User user = optionalUser.orElseThrow();

        //Se transforma los roles de la DB a roles de Spring
        List<GrantedAuthority> authorities = user.getRoles()
            .stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toList());

        // Crea y retorna un objeto UserDetails (el usuario de Spring Security)
        return new org.springframework.security.core.userdetails.User(
            username,
            user.getPassword(),
            true,
            true,
            true,
            true,
            authorities);
}
    
}
