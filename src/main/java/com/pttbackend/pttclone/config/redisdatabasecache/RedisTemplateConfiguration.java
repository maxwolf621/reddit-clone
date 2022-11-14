package com.pttbackend.pttclone.config.redisdatabasecache;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Configure StringTemplate and RedisTemplate
 */
@Configuration
public class RedisTemplateConfiguration {

    @Bean(name = "redisServerStringRedisTemplate")
    public StringRedisTemplate redisServer1StringRedisTemplate( 
        @Qualifier("redisServerConnectionFactory") RedisConnectionFactory lettuceConnectionFactory)
    {        
        return new StringRedisTemplate(lettuceConnectionFactory);
    }

    @Bean(name = "redisServerRedisTemplate")
    public <K,T> RedisTemplate<K,T> redisServerRedisTemplate(
        @Qualifier("redisStandAloneServerConnectionFactory")  RedisConnectionFactory lettuceConnectionFactory)
    {
        var redisTemplate = new RedisTemplate<K,T>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
    
        this.setSerializer(redisTemplate);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * de / serializer for key value pair
     * @param <K, T>
     * @param redisTemplate
     */
    private <K,T> void setSerializer(RedisTemplate<K, T> redisTemplate) {
        redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        
        // Key
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // value
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
    }

}
