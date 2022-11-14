package com.pttbackend.pttclone.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.pttbackend.pttclone.config.databaseconfiguration.DataSourceContextHolder;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class SlaveServerAOP { 
    @Around("@annotation(com.pttbackend.pttclone.annotation.ReadDS)")
    public Object aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {

        try{
            DataSourceContextHolder.createThread();
            return joinPoint.proceed();
        }finally{
            DataSourceContextHolder.releaseThread();
        }
    }
}