package com.pttbackend.pttclone.config.databaseconfiguration.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="hikari.read.conf")
public class HakariReadProperties extends HakariProperties {
}
