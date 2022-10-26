package com.pttbackend.pttclone.config.redisdatabasecache;

import java.time.Duration;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

import lombok.RequiredArgsConstructor;

/**
 * RedisConnection Factory
 */
@Configuration
@RequiredArgsConstructor 
class RedisConnectionFactories {

    private final RedisServer1Property redisServer1Property;

    @Bean(name = "redisServer1ConnectionFactory")
    public LettuceConnectionFactory redisServer1LettuceConnectionFactory(){
        var redisServerConfig = new RedisStandaloneConfiguration();
        
        // redis database server configuration
        redisServerConfig.setHostName(redisServer1Property.getHost());
        redisServerConfig.setPort(redisServer1Property.getPort());
        redisServerConfig.setDatabase(redisServer1Property.getDatabase());

        // pool config 
        var poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(redisServer1Property.getMaxActive());
        poolConfig.setMaxIdle(redisServer1Property.getMaxIdle());

        // builder with redis Server Configuration and Pool Configuration
        var clientConfig = LettucePoolingClientConfiguration.builder()
                                .commandTimeout(Duration.ofMillis(100))
                                .poolConfig(poolConfig).build();

        return new LettuceConnectionFactory(redisServerConfig,clientConfig);
    }
}

