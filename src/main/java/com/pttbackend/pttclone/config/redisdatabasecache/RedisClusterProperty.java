package com.pttbackend.pttclone.config.redisdatabasecache;

import java.util.Set;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@Data
@ConfigurationProperties(prefix = "spring.redis.cluster")
public class RedisClusterProperty {
    private Set<String> nodes;
    private String password;
    private int maxRedirects;
}
