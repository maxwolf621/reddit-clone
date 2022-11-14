package com.pttbackend.pttclone.config.redisdatabasecache;

import java.time.Duration;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RedisConnection Factory
 */
@Configuration
@AllArgsConstructor 
@Slf4j
class RedisConnectionFactories {

    private final RedisServerProperty redisServerProperty;
    private final RedisClusterProperty redisClusterProperty;


    @Bean(name = "redisStandAloneServerConnectionFactory")
    public LettuceConnectionFactory redisServer1LettuceConnectionFactory(){
        var redisServerConfig = new RedisStandaloneConfiguration();

        // redis database server configuration
        redisServerConfig.setHostName(redisServerProperty.getHost());
        redisServerConfig.setPort(redisServerProperty.getPort());
        redisServerConfig.setDatabase(redisServerProperty.getDatabase());

        // pool config 
        var poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(redisServerProperty.getMaxActive());
        poolConfig.setMaxIdle(redisServerProperty.getMaxIdle());

        // Client Configuration
        var clientConfig = LettucePoolingClientConfiguration.builder()
                                //.commandTimeout(Duration.ofMillis(10000))
                                .poolConfig(poolConfig).build();

        return new LettuceConnectionFactory(redisServerConfig,clientConfig);
    }

    @Primary
    @Bean(name = "redisServerConnectionFactory")
    public LettuceConnectionFactory redisClusterLettuceConnectionFactory(){
        RedisClusterConfiguration redisClusterConfig = new RedisClusterConfiguration(redisClusterProperty.getNodes());
        redisClusterConfig.setMaxRedirects(redisClusterProperty.getMaxRedirects());

        var poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(redisServerProperty.getMaxActive());
        poolConfig.setMaxIdle(redisServerProperty.getMaxIdle());
        // poolConfig.setMinIdle(redisServerProperty.getMinIdle());
        // poolConfig.setMaxWaitMillis(redisServerProperty.getSeconds());

        // Active Topology Refresh
        ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
            .enableAllAdaptiveRefreshTriggers()
            // trigger
            .enableAdaptiveRefreshTrigger()
            // interval for refresh
            .enablePeriodicRefresh(Duration.ofSeconds(5))
            .build();

        ClusterClientOptions clusterClientOptions = ClusterClientOptions.builder().topologyRefreshOptions(clusterTopologyRefreshOptions).build();

        LettuceClientConfiguration lettuceClientConfiguration = LettucePoolingClientConfiguration.builder()
            .poolConfig(poolConfig)
            //.readFrom(ReadFrom.SLAVE_PREFERRED)  // read write splitting
            //.commandTimeout(Duration.ofMillis(10)) // RedisURI.DEFAULT_TIMEOUT 10
            .clientOptions(clusterClientOptions).build();

        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(
            redisClusterConfig, lettuceClientConfiguration);
    
        return lettuceConnectionFactory;
    }
}

