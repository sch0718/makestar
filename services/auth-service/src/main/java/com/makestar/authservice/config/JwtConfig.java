package com.makestar.authservice.config;

import com.makestar.utils.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;
    
    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;
    
    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils(secret, accessTokenExpiration, refreshTokenExpiration);
    }
} 