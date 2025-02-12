package com.springboot.backend.address.userapp.users_backend.auth.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.backend.address.userapp.users_backend.entities.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static com.springboot.backend.address.userapp.users_backend.auth.TokenJwtConfig.*;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        
        String userName = null;
        String password = null;
        
        try {
            //Obtenemos el usuario y la contrsaseña del usuario
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            userName = user.getUserName();
            password = user.getPassword();
        } catch (StreamReadException e) {
            e.printStackTrace();
        } catch (DatabindException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //Genera token de autenticación
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userName, password); 

        return this.authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authResult
                .getPrincipal();
        String userName = user.getUsername();
        Collection<? extends GrantedAuthority> roles = authResult.getAuthorities(); //Obtenemos los roles de usuario

        Claims claims = Jwts.claims()
            .add("authorities", new ObjectMapper().writeValueAsString(roles))
            .add("username", userName)
                .build();

        //Creación del JWT (JSON Web Token)
        String jwt = Jwts.builder()
                .subject(userName)
                .claims(claims)
                .signWith(SECRET_KEY)
                .issuedAt(new Date()) //Fecha de creación
                .expiration(new Date(System.currentTimeMillis() + 3600000)) //Expiración de la sesión en 1 hora en milisegundos
                .compact();

        response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + jwt);

        //Datos para el JSON 
        Map<String, String> body = new HashMap<>();
        body.put("token", jwt);
        body.put("username", userName);
        body.put("message", String.format("%s, has iniciado sesión con exito", userName));

        //Conversión de datos a JSON
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setContentType(CONTENT_TYPE);
        response.setStatus(200);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {

        // Datos para el JSON
        Map<String, String> body = new HashMap<>();
        body.put("message", "Usuario o contraseña incorrecta");
        body.put("error", failed.getMessage());

        // Conversión de datos a JSON
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setContentType(CONTENT_TYPE);
        response.setStatus(401);
    }
    
    
    
}
