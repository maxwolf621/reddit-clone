package com.pttbackend.pttclone.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;


@Aspect
public class SystemArchitecture {

  @Pointcut("within(com.pttbackend.pttclone.service.*)")
  public void inServiceLayer() { /*TODO Service Layer Pointcut*/ }

  @Pointcut("within(com.pttbackend.pttclone.controller.*)")
  public void inControllerLayer() {/* TODO Controller Layer Pointcut */}

  @Pointcut("within(com.pttbackend.pttclone.config.redisdatabasecache.RedisTemplateConfiguration)")
  public void redisConfigurationLogs(){ /* TODO Redis Configuration */}
}
