package com.pttbackend.pttclone.config.redisdatabasecache;

import com.pttbackend.pttclone.model.RedisProperty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class RedisServerProperty extends RedisProperty{
}
