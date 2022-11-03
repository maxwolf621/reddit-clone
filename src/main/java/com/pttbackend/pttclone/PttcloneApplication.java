package com.pttbackend.pttclone;

import com.pttbackend.pttclone.config.OAuth2Properties;
import com.pttbackend.pttclone.config.StorageProperties;

import springfox.documentation.oas.annotations.EnableOpenApi;

// import springfox.documentation.oas.annotations.EnableOpenApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 * <h2>Application for my spring project</h2>
 * @see  org.springframework.context.annotation.ComponentScan
 * @see  org.springframework.context.annotation.Import
 * @see  <a href="https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/autoconfigure/SpringBootApplication.html">
 * 		 org.springframework.boot.autoconfigure.SpringBootApplication</a>
 * @see  <a href="https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/context/properties/EnableConfigurationProperties.html">
 * 		 org.springframework.boot.context.properties.EnableConfigurationProperties </a>
 * @see  org.springframework.scheduling.annotation.EnableAsync
 */
@EnableOpenApi
@SpringBootApplication
@EnableConfigurationProperties({OAuth2Properties.class, 
								StorageProperties.class})
@EnableAsync
@EnableScheduling
@EnableAspectJAutoProxy
public class PttcloneApplication {
    
	public static void main(String[] args) {
		SpringApplication.run(PttcloneApplication.class, args);
	}

}