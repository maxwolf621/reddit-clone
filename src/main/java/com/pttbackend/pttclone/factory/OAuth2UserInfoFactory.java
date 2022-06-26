package com.pttbackend.pttclone.factory;
import java.util.Map;

import com.pttbackend.pttclone.exceptions.OAuth2UserInfoAuthenticationException;
import com.pttbackend.pttclone.model.AuthProviderType;
import com.pttbackend.pttclone.oauth2userinfo.GitHubUserInfo;
import com.pttbackend.pttclone.oauth2userinfo.GoogleUserInfo;
import com.pttbackend.pttclone.oauth2userinfo.OAuth2UserInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * <p> Create A userInfo from Third Party </p>
 */
@Slf4j
public class OAuth2UserInfoFactory {
    
    private OAuth2UserInfoFactory(){

    }
    
    /**
     * Get UserInfo from GITHUB or GOOGLE
     * @param registrationId : Provider's name (e.g. GITHUB, GOOGLE, etc ...)
     * @param claims : Attributes from 3rd party resource
     * @return {@code Oauth2UserInfo}
     * @throws com.pttbackend.pttclone.exceptions.OAuth2UserInfoAuthenticationException
     */
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> claims){
        if(registrationId.equalsIgnoreCase(AuthProviderType.GOOGLE.toString())){
            log.info("Login via Google Account");
            log.info("Claims : " + claims.toString());
            return new GoogleUserInfo(claims);
        }
        else if(registrationId.equalsIgnoreCase(AuthProviderType.GITHUB.toString())){
            log.info("Login via Github Account");
            log.info("Claims : " + claims.toString());
            return new GitHubUserInfo(claims);
        }
        else{
            throw new OAuth2UserInfoAuthenticationException("Login with " + registrationId + " is not supported yet.");
        }
    }
}
