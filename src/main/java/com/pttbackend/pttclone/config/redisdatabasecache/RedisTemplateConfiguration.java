package com.pttbackend.pttclone.config.redisdatabasecache;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Configure StringTemplate and RedisTemplate
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisTemplateConfiguration {

    @Bean(name = "redisServer1StringRedisTemplate")
    public StringRedisTemplate redisServer1StringRedisTemplate( @Qualifier("redisServer1ConnectionFactory") RedisConnectionFactory lettuceConnectionFactory){
        
        log.info("-- RedisTemplateConfiguration redisServer1StringRedisTemplate");
        
        return new StringRedisTemplate(lettuceConnectionFactory);
    }

    @Bean(name = "redisServer1RedisTemplate")
    public <T> RedisTemplate<String , T> redisServer1RedisTemplate (@Qualifier("redisServer1ConnectionFactory") RedisConnectionFactory lettuceConnectionFactory ){
        
        var redisTemplate = new RedisTemplate<String, T >();

        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        
        this.setSerializer(redisTemplate);

        redisTemplate.afterPropertiesSet();

        log.info("-- RedisTemplateConfiguration for redisServer1RedisTemplate");

        return redisTemplate;

    }

    /**
     * de / serializer for key value pair
     * @param <T>
     * @param redisTemplate
     */
    private <T> void setSerializer(RedisTemplate<String, T> redisTemplate) {
        redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        
        // Key
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // value
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
    }

}
