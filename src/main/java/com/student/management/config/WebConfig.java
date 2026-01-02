package com.student.management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebConfig - Additional Web MVC configurations
 * 
 * NOTE: CORS is handled by SecurityConfig.java which uses allowedOriginPatterns
 * to support wildcards like *.vercel.app, *.railway.app, etc.
 * Do NOT add CORS mappings here to avoid conflicts.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // CORS is configured in SecurityConfig.java with proper patterns for:
    // - http://localhost:*
    // - https://*.vercel.app
    // - https://*.railway.app
    // - https://*.netlify.app
}
