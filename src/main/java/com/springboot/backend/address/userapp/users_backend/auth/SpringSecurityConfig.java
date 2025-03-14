package com.springboot.backend.address.userapp.users_backend.auth;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.springboot.backend.address.userapp.users_backend.auth.filter.JwtAuthenticationFilter;
import com.springboot.backend.address.userapp.users_backend.auth.filter.JwtValidationFilter;

@Configuration // Indica que esta clase contiene definiciones de beans de Spring
public class SpringSecurityConfig {
    
    @Autowired //Inyectamos
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); 
    }

    @Bean // Define un bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(authz -> authz // Configura las autorizaciones para las peticiones HTTP
                //USERS HTTP REQUESTS
                .requestMatchers(HttpMethod.GET, "/api/users", "/api/users/page/{page}").permitAll() // Permite el acceso a esas rutas GET sin autenticación
                .requestMatchers(HttpMethod.GET, "/api/users/{id}").hasAnyRole("USER", "ADMIN") //Permite el acceso a esta ruta a los dos roles            
                .requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN") //Permite el acceso a esta ruta POST a los roles ADMIN                  
                .requestMatchers(HttpMethod.PUT, "/api/users/{id}").hasRole("ADMIN") //Permite el acceso a esta ruta PUT a los roles ADMIN                  
                .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasRole("ADMIN")//Permite el acceso a esta ruta DELETE a los roles ADMIN                  
                //POSTS HTTP REQUESTS
                .requestMatchers(HttpMethod.POST, "/api/posts").hasAnyRole("USER", "ADMIN") //Permite el acceso a esta ruta DELETE a los roles ADMIN                  
                .requestMatchers(HttpMethod.GET, "/api/posts").hasAnyRole("USER", "ADMIN") //Permite el acceso a esta ruta DELETE a los roles ADMIN                  
                
                .anyRequest().authenticated()) // Requiere autenticación para cualquier otra petición
                .cors(cors -> cors.configurationSource(configurationSource())) // Es el que permite que otros dominios (en este caso el sitio web hecho en Angular) pueda acceder a los recursos de Spring Security 
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtValidationFilter(authenticationManager()))
                .csrf(config -> config.disable()) // Deshabilita la protección CSRF (Cross-Site Request Forgery)
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Configura la gestión de sesiones como STATELESS (sin estado)
                .build(); // Construye y retorna el metodo
    }

    @Bean
    CorsConfigurationSource configurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        config.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        config.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> corsBean = new FilterRegistrationBean<CorsFilter>(
                new CorsFilter(this.configurationSource()));
        corsBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return corsBean;
    }
}