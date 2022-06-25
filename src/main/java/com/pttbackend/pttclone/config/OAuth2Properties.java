package com.pttbackend.pttclone.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Data;
import java.util.List;

/**
 * <p> LogIn via mobile or computer </p>
 * @see <a href="https://matthung0807.blogspot.com/2020/09/spring-boot-configurationproperties-constructorbinding.html"> 
 *      usage of ConfigurationProperties </a>
 */
@ConstructorBinding
@ConfigurationProperties(prefix = "app")
@Data
public class OAuth2Properties {
    private List<String> authorizedRedirectUris;
}
