package com.pttbackend.pttclone.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Data;

@ConstructorBinding
@ConfigurationProperties(prefix = "storage")
@Data
public class StorageProperties {    
    /**
     * Directory path where stores the
     * uploaded files 
     */
    private String location ;

}
