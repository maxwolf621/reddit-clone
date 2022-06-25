package com.pttbackend.pttclone.model;

import lombok.Data;

@Data
public class RedisProperty {
    private String host;
    private int port;
    private int database;
    private int maxActive;
    private int maxWait;
    private int maxIdle;
    private int timeout;
}
