package com.pttbackend.pttclone.config.redisdatabasecache;

import com.pttbackend.pttclone.model.RedisProperty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.redis1")
public class RedisServer1Property extends RedisProperty{
}
