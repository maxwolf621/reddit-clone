package com.pttbackend.pttclone.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * Exception for Oauth2UserInfo
 */
public class OAuth2UserInfoAuthenticationException extends AuthenticationException {

    public OAuth2UserInfoAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public OAuth2UserInfoAuthenticationException(String message) {
        super(message);
    }
}
