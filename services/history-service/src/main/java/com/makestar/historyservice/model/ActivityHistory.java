package com.makestar.historyservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_histories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityHistory {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    @Column(nullable = false)
    private String userId;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActivityType activityType;
    
    @Column(nullable = false)
    private String entityId;
    
    @Column
    private String entityType;
    
    @Column
    private String description;
    
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    @Column
    private String ipAddress;
    
    @Column
    private String userAgent;
    
    public enum ActivityType {
        LOGIN, LOGOUT, VIEW_PROFILE, UPDATE_PROFILE, 
        CREATE_POST, UPDATE_POST, DELETE_POST, LIKE_POST, 
        CREATE_COMMENT, UPDATE_COMMENT, DELETE_COMMENT,
        SEND_MESSAGE, READ_MESSAGE, CREATE_CHAT_ROOM, JOIN_CHAT_ROOM, LEAVE_CHAT_ROOM,
        SEND_FRIEND_REQUEST, ACCEPT_FRIEND_REQUEST, REJECT_FRIEND_REQUEST, REMOVE_FRIEND,
        SYSTEM_ERROR, OTHER
    }
} 