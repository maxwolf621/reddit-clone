package com.pttbackend.pttclone.config.threadpool;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Data
@ConfigurationProperties(prefix = "spring.threadpool")
public class ThreadPoolConfiguration{ 
private Integer corePoolSize;
private Integer maxPoolSize;
private Integer queueCapacity;
private Integer keepAliveSeconds;
}
