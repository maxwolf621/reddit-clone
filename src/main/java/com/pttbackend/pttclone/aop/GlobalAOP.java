package com.pttbackend.pttclone.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
public class GlobalAOP {
    
    @Before("com.pttbackend.pttclone.aop.SystemArchitecture.inControllerLayer()")
    public void callMethod(JoinPoint joinPoint){
        log.info("******************** Calling " + joinPoint.getSignature().getName());
    }

    @Before("com.pttbackend.pttclone.aop.SystemArchitecture.redisConfigurationLogs()")
    public void redisDatabaseLogs(JoinPoint joinPoint){
        log.info("Redis Configuration Successfully");
    }

}
