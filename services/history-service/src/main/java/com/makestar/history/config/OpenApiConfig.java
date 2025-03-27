package com.makestar.history.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI 설정 클래스
 * 
 * <p>히스토리 서비스의 API 문서화를 위한 SpringDoc OpenAPI 설정을 정의합니다.</p>
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
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
            .info(new Info()
                .title("MakeStar 히스토리 서비스 API")
                .description("MakeStar 플랫폼의 히스토리 관련 API를 제공합니다.")
                .version("v1.0.0")
                .contact(new Contact()
                    .name("MakeStar Team")
                    .email("support@makestar.com")
                    .url("https://makestar.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")))
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(new Components()
                .addSecuritySchemes(securitySchemeName, 
                    new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
} 