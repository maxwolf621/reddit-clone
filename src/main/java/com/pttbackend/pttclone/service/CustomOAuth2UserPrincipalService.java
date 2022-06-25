package com.pttbackend.pttclone.service;

import org.springframework.stereotype.Service;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.util.Collections.singletonList;

import com.pttbackend.pttclone.factory.OAuth2UserInfoFactory;
import com.pttbackend.pttclone.oauth2userinfo.OAuth2UserInfo;
import com.pttbackend.pttclone.security.OAuth2UserPrincipal;


/**
 * <h3> CustomOauth2UserService </h3>
 * <p>  A Custom Oauth2User (Authenticated) Principal by {@code #loadUser(OAuth2UserRequest)} </p>
 * @see <a href="https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/userinfo/DefaultOAuth2UserService.java">
 *      Example of {@code DefaultOauth2UserServer} </a>
 * @see <a href="https://www.codota.com/code/java/methods/org.springframework.security.oauth2.core.user.DefaultOAuth2User/%3Cinit%3E">
 *      Example of {@code DefaultOauth2User} </a>
 */
@Service
@AllArgsConstructor
@Slf4j
public class CustomOAuth2UserPrincipalService extends DefaultOAuth2UserService {

    /**
     * @param oAuth2UserRequest {@link OAuth2UserRequest}
     * @throws OAuth2AuthenticationException if {@link OAuth2UserRequest} is not valid
     * @return 
     * {@link com.pttbackend.pttclone.security.OAuth2UserPrincipal#OAuth2UserPrincipal(java.util.Collection, java.util.Map, String, OAuth2UserInfo)}
     * 
     * @see DefaultOAuth2User#DefaultOAuth2User(java.util.Collection authorities, java.util.Map userAttributes, String userNameAttributeName)
     * ,this Class for Returning A Authenticated Authentication
     * <p> {@code userAttributes} UserPrincipal for {@link OAuth2UserInfo#OAuth2UserInfo(java.util.Map)} 
     *      contained userinfo from third party application (name, emails, ... ) </p>
     * <p> {@code userNameAttributeName} 
     *      is the attribute {@code name} that references the Name or Identifier of the end-user
     *      stored in {@code userAttributes} </p>
     * <p> {@code authroities} the Role of the User e.g. USER, ADMIN ... </P>
     * 
     * @see OAuth2UserRequest#getClientRegistration()
     * @see ClientRegistration#getRegistrationId()
     * @see OAuth2UserInfoFactory#getOAuth2UserInfo(String, java.util.Map)}
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        log.info("------------- CustomOauth2User Service -------------");
        
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        log.info("___The Third Party User Protected Resources (Attributes): " +  oAuth2User);
        
        String authProvider = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        log.info("___The Authentication Provider is: " + authProvider);
        
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(authProvider, oAuth2User.getAttributes());
        
        try{      
            log.info("____RETURN OAuth2UserPrincipal for the Authentication");
            return new OAuth2UserPrincipal(singletonList(new SimpleGrantedAuthority("USER")), 
                                                                    oAuth2User.getAttributes(), 
                                                                    "name",
                                                                    userInfo);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // @throw an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    } 

}
