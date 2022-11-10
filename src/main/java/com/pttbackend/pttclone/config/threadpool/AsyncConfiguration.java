package com.pttbackend.pttclone.config.threadpool;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfiguration  implements AsyncConfigurer{
    private static final String TASK_EXECUTOR_DEFAULT = "taskExecutor";
    private static final String TASK_EXECUTOR_NAME_PREFIX_DEFAULT = "taskExecutor-";
    private static final String TASK_EXECUTOR_NAME_PREFIX_REPOSITORY = "serviceTaskExecutor-";
    private static final String TASK_EXECUTOR_NAME_PREFIX_CONTROLLER = "controllerTaskExecutor-";
    private static final String TASK_EXECUTOR_NAME_PREFIX_SERVICE = "serviceTaskExecutor-";

    public static final String TASK_EXECUTOR_REPOSITORY = "repositoryTaskExecutor";
    public static final String TASK_EXECUTOR_SERVICE = "serviceTaskExecutor";
    public static final String TASK_EXECUTOR_CONTROLLER = "controllerTaskExecutor";

    @Override
    @Bean(name = TASK_EXECUTOR_DEFAULT)
    public Executor getAsyncExecutor() {
        return taskExecutor(TASK_EXECUTOR_NAME_PREFIX_DEFAULT);
    }

    @Bean(name = TASK_EXECUTOR_REPOSITORY)
    public Executor getRepositoryAsyncExecutor() {
        return taskExecutor(TASK_EXECUTOR_NAME_PREFIX_REPOSITORY);
    }

    @Bean(name = TASK_EXECUTOR_SERVICE)
    public Executor getServiceAsyncExecutor() {
        return taskExecutor(TASK_EXECUTOR_NAME_PREFIX_SERVICE);
    }

    @Bean(name = TASK_EXECUTOR_CONTROLLER)
    public Executor getControllerAsyncExecutor() {
        return taskExecutor(TASK_EXECUTOR_NAME_PREFIX_CONTROLLER);
    }

    private final ThreadPoolConfiguration threadPoolConfiguration;
    public AsyncConfiguration(final ThreadPoolConfiguration threadPoolConfiguration){
        this.threadPoolConfiguration = threadPoolConfiguration;
    }

     /**
     * @return the ThreadPoolTaskExecutor.
     * @apiNote CorePoolSize core size in pool.
     * @apiNote MaxPoolSize  max core size in pool.
     * @apiNote queueCapacity task queue size
     * @apiNote namePrefix thread name prefix
     * @apiNote keepAliveSeconds become thread has free, it's keep alive seconds.
     * @see ThreadPoolExecutor RejectedExecutionHandler
     */ 
    public DelegatingSecurityContextAsyncTaskExecutor taskExecutor(final String taskExecutorNamePrefix) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolConfiguration.getCorePoolSize());
        executor.setMaxPoolSize(threadPoolConfiguration.getMaxPoolSize());
        executor.setQueueCapacity(threadPoolConfiguration.getQueueCapacity());
        executor.setKeepAliveSeconds(threadPoolConfiguration.getKeepAliveSeconds());
        executor.setThreadNamePrefix(taskExecutorNamePrefix);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        //  Delegating Security ContextAsync Async Task
        return new DelegatingSecurityContextAsyncTaskExecutor(executor);
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (Throwable ex, Method method, Object... params)->{
            log.error("class#method: " + method.getDeclaringClass().getName() + "#" + method.getName());
            log.error("type        : " + ex.getClass().getName());
            log.error("exception   : " + ex.getMessage());
        };
    }
}
