package com.pttbackend.pttclone.aop;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
public class GlobalAOP {
    
    @Before("com.pttbackend.pttclone.aop.SystemArchitecture.inControllerLayer()")
    public void callMethod(JoinPoint joinPoint){
        log.info("******************** Invocation " + joinPoint.getSignature().getName());
        Arrays.stream(joinPoint.getArgs()).forEach(e -> log.info("Args : " + e));
        // MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    }

    @After("com.pttbackend.pttclone.aop.SystemArchitecture.redisConfigurationLogs()")
    public void redisDatabaseLogs(JoinPoint joinPoint){
        log.info("Redis Configuration Successfully");
    }
}
