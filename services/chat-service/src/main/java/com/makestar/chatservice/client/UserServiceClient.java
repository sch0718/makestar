package com.makestar.chatservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/users/{userId}")
    ResponseEntity<Map<String, Object>> getUserById(@PathVariable("userId") String userId);

    @GetMapping("/api/users/username/{username}")
    ResponseEntity<Map<String, Object>> getUserByUsername(@PathVariable("username") String username);
} 