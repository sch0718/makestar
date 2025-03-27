package com.makestar.discovery.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI 설정 클래스
 * 
 * <p>디스커버리 서비스의 API 문서화를 위한 SpringDoc OpenAPI 설정을 정의합니다.</p>
 */
@Configuration
public class OpenApiConfig {

    /**
     * OpenAPI 구성을 정의하는 빈
     * 
     * @return 구성된 OpenAPI 객체
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("MakeStar 디스커버리 서비스 API")
                .description("MakeStar 플랫폼의 서비스 검색 관련 API를 제공합니다.")
                .version("v1.0.0")
                .contact(new Contact()
                    .name("MakeStar Team")
                    .email("support@makestar.com")
                    .url("https://makestar.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
} 