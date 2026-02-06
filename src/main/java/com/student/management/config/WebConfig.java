package com.student.management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // CORS configuration removed - handled by SecurityConfig.java
    // to prevent conflicts between WebMVC CORS and Spring Security CORS

}
