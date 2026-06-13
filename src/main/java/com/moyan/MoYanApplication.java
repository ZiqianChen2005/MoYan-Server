package com.moyan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MoYanApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoYanApplication.class, args);
        System.out.println("========== 陌言服务端启动成功 ==========");
        System.out.println("HTTP端口: 8888");
        System.out.println("WebSocket端点: ws://localhost:8888/ws");
        System.out.println("Knife4j文档: http://localhost:8888/doc.html");
        System.out.println("http API基础路径: http://localhost:8888/api");
        System.out.println("===================================");
    }
}
