package com.pttbackend.pttclone.config.databaseconfiguration.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
public class HakariProperties {
    private Boolean autoCommit;
    private Integer connectionTimeout;
    private Integer idleTimeout;
    private Integer maxLifetime;
    private Integer minimumIdle;
    private Integer maximumPoolSize;
};
