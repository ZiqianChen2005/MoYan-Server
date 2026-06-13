package com.moyan.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("陌言服务端 API 文档")
                        .version("2.1-SNAPSHOT")
                        .description("陌言服务端 - 基于 Socket + RESTful API 的文学社交平台\n\n" +
                                "**项目简介**：陌言是一个文学创作与交流平台，支持诗歌、散文等文学作品的发布、交流、评分和打赏。\n\n" +
                                "**技术栈**：Spring Boot 3.1.5 + JDK 20 + SQL Server + WebSocket\n\n" +
                                "**通信方式**：\n" +
                                "- WebSocket: ws://localhost:8888/ws\n" +
                                "- RESTful API: http://localhost:8888/api\n\n"+
                                "***注意：由于我们使用的是websocket通信，接口地址仅为示意，实际仍根据action内容判断请求。***")
                        .contact(new Contact()
                                .name("MoYan Team")
                                .email("support@moyan.com")));
    }
}
