package com.makestar.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.beans.factory.annotation.Value;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * MongoDB 설정 클래스
 * 
 * <p>MongoDB 연결 및 관련 설정을 담당하는 설정 클래스입니다.</p>
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.makestar.chat.repository")
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database:chatdb}")
    private String databaseName;

    /**
     * MongoDB 데이터베이스 이름을 반환합니다.
     *
     * @return MongoDB 데이터베이스 이름
     */
    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    /**
     * MongoDB 클라이언트를 생성합니다.
     *
     * @return MongoDB 클라이언트 인스턴스
     */
    @Override
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }
} 