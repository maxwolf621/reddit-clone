package com.pttbackend.pttclone.config;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import com.github.benmanes.caffeine.cache.Caffeine;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.Nullable;

import lombok.extern.slf4j.Slf4j;

@EnableCaching
@Configuration
@Slf4j
public class CacheConfig extends CachingConfigurerSupport {

    @Bean
    public Caffeine<Object,Object> caffeineConfig(){
        return Caffeine.newBuilder()
                       .expireAfterAccess(60, TimeUnit.SECONDS)
                       .initialCapacity(200)
                       .maximumSize(500)
                       .weakKeys()
                       .recordStats();
    }

    /**
     * caffeine provider
     * @param caffeine Configuration for caffeine cache provider
     * @return a cache cache provider
     */
    @Bean(name = "caffeineCacheManager")
    @Primary
    public CacheManager caffeineManager(Caffeine<Object,Object> caffeine) {

        log.info("---- Caffeine Manager ----");

        // CaffeineCacheManager(String... cacheNames)
        // Construct a static CaffeineCacheManager, managing caches for the specified cache customer and orders
        // e.g. @Cacheable(cacheNames = customers , ...) or @Cacheable(cacheNames = orders , ...)
        var cacheManager = new CaffeineCacheManager();


        cacheManager.setCaffeine(caffeine);
        
        return cacheManager;
    }

    /**
     * redis Cache for server 1
     * @param lettuceConnectionFactory
     * @return a redis cache provider
     */
    @Bean(name = "redisServer1CacheManager")
    public CacheManager redisServer1CacheManager(@Qualifier("redisServer1ConnectionFactory") RedisConnectionFactory lettuceConnectionFactory){

        log.info("---- Redis Cache Manager ----");
        var redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofMinutes(30));

        Set<String> cacheNames = new HashSet<String>() {};
        cacheNames.add("post");


        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(lettuceConnectionFactory))
                                .disableCreateOnMissingCache()
                                .cacheDefaults(redisCacheConfiguration)
                                .transactionAware()
                                .enableStatistics()
                                .initialCacheNames(cacheNames)
                                .build();
    }

    /**
     * if the class's method has no parameter return {@code SimpleKey#EMPTY}
     * if the class's method has parameters return ClassName + MethodName + Params
     */
    @Bean
    @Nullable
    @Override
    public KeyGenerator keyGenerator() {
    
        return (target, method, params) -> {

            if (params.length == 0) {
                log.info("Return SimpleKey.Empty");
                return SimpleKey.EMPTY;
            }
        
            log.info("parameter " + Stream.of(params).toString().isBlank());

            StringBuilder sb = new StringBuilder();

            log.info("Class Name : " + target.getClass().getName());
            sb.append(target.getClass().getName());

            log.info("Method Name : " + method.getName());
            sb.append(method.getName());

            for (Object obj : params) {
                log.info(obj.toString());
                sb.append(obj.toString());
            }

            log.info(" --- KeyGenerator : " + sb.toString());
            return sb.toString();
        };
    }
}
