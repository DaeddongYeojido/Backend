package com.daeddong.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("대똥여지도 API")
                        .description("""
                                공용화장실 위치 기반 모바일 서비스 백엔드 API
                                
                                **인증**: 로그인 없이 deviceId(기기 고유 ID)로 사용자 식별
                                
                                **이미지 업로드**: multipart/form-data — data 파트(JSON) + image 파트(파일)
                                """)
                        .version("v1")
                        .contact(new Contact().name("대똥여지도 팀")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("로컬 서버"),
                        new Server().url("https://api.daeddong.com").description("운영 서버")
                ));
    }
}
