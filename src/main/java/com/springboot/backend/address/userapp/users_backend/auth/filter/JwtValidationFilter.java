package com.springboot.backend.address.userapp.users_backend.auth.filter;

import static com.springboot.backend.address.userapp.users_backend.auth.TokenJwtConfig.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.backend.address.userapp.users_backend.auth.SimpleGrantedAuthorityJsonCreator;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtValidationFilter extends BasicAuthenticationFilter{

    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String header = request.getHeader(HEADER_AUTHORIZATION); // Obtiene el valor del encabezado de autorización (donde se espera el token JWT)

        if (header == null || !header.startsWith(PREFIX_TOKEN)) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.replace(PREFIX_TOKEN, ""); // Extrae el token JWT sin el prefijo.

        try {
            Claims claims = Jwts.parser() // Crea un analizador de tokens JWT.
                    .verifyWith(SECRET_KEY) // Establece la clave secreta para verificar la firma del token.
                    .build()
                    .parseSignedClaims(token) // Parsea y verifica el token.
                    .getPayload(); // Obtiene el cuerpo (payload) del token, que contiene las reclamaciones (claims).

            //String username = (String) claims.get("username"); //Alternativa para obtener usuario        
            String username = claims.getSubject(); //Obtiene el usuario
            Object authoritiesClaims = claims.get("authorities"); //Obtiene los roles de usuario

            //Almacenamos los roles en un Colletion
            Collection<? extends GrantedAuthority> roles = Arrays.asList(new ObjectMapper()
            .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class)
            .readValue(authoritiesClaims.toString().getBytes(), SimpleGrantedAuthority[].class)); 

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,null,
                    roles);

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            chain.doFilter(request, response);

        } catch (JwtException e) {
            Map<String, String> body = new HashMap<>();
            body.put("error", e.getMessage()); 
            body.put("message", "El token no es valido");
            
            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            response.setStatus(401);
            response.setContentType(CONTENT_TYPE);
        }
    }
}
