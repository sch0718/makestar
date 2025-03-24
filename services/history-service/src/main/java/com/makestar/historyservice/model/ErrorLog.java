package com.makestar.historyservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "error_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorLog {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    @Column(nullable = false)
    private String serviceName;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ErrorLevel errorLevel;
    
    @Column(nullable = false)
    private String errorCode;
    
    @Column(nullable = false)
    private String message;
    
    @Column
    @Lob
    private String stackTrace;
    
    @Column
    private String userId;
    
    @Column
    private String requestUrl;
    
    @Column
    private String requestMethod;
    
    @Column
    private String requestParams;
    
    @Column
    private String ipAddress;
    
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    public enum ErrorLevel {
        INFO, WARNING, ERROR, CRITICAL
    }
} 