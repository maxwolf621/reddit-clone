package com.pttbackend.pttclone.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * CORS setup
 * @see <a href="https://github.com/maxwolf621/SpringNote/blob/main/SpringBootCORS.md"> Details </a>
 */
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedOrigins("http://localhost:4200")
                //.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedMethods("*")
                .maxAge(3600L)   
                .allowedHeaders("*")
                // allow header : Authorization
                .exposedHeaders("Authorization")
                //allow Cookie
                .allowCredentials(true);
    }
}