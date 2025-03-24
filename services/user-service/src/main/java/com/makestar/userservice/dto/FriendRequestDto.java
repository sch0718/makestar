package com.makestar.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestDto {
    private String id;
    private String senderId;
    private String receiverId;
    private String status; // PENDING, ACCEPTED, REJECTED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 