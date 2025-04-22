package com.demo.transaction.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    // 前后端开发和测试使用
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 允许所有路径
                .allowedOrigins("http://localhost:5173")  // 允许的前端地址
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")  // 允许的 HTTP 方法
                .allowedHeaders("Authorization", "Content-Type", "Accept", "X-Requested-With")  // 允许的请求头
                .allowCredentials(true) // 是否允许发送 Cookie
                .maxAge(3600);  // 预检请求的有效期（单位：秒）;
    }
}