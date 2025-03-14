package com.springboot.backend.address.userapp.users_backend.auth;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtUtil {

    //Ayuda a extraer el username del token que entrega el frontend en el encabezado de la petición
    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = this.extractAllClaims(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    //Extrae todos los claims del token
    private Claims extractAllClaims(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(TokenJwtConfig.SECRET_KEY).build().parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

}