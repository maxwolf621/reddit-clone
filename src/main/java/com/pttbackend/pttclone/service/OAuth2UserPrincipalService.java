package com.pttbackend.pttclone.service;

import org.springframework.stereotype.Service;

import com.pttbackend.pttclone.exceptions.OAuth2UserInfoAuthenticationException;
import com.pttbackend.pttclone.factory.OAuth2UserInfoFactory;
import com.pttbackend.pttclone.model.User;
import com.pttbackend.pttclone.oauth2userinfo.OAuth2UserInfo;
import com.pttbackend.pttclone.repository.UserRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import static java.util.Collections.singletonList;

/**
 * Code of DefaultOAuth2UserService
 * https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/userinfo/DefaultOAuth2UserService.java
 * Example of DefaultOAuth2User
 * https://www.codota.com/code/java/methods/org.springframework.security.oauth2.core.user.DefaultOAuth2User/%3Cinit%3E
*/


/**
 * @Deprecated
 *  1. Log In/Sing Up via third-party application
 *  2. Update User details in Local 
 *  3. Return the authenticated user 
 * */
@Service
@AllArgsConstructor
@Slf4j
public class OAuth2UserPrincipalService extends DefaultOAuth2UserService{
    
    private final UserRepository userRepo;

    // oAuth2userRequest contains clientRegistration,token, additional parameter
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        /**
         * return DefaultOAuth2User(authorities, userAttributes, userNameAttributeName);
         * userNameAttributeName : The name of the attribute returned in the UserInfo Response that references the Name or Identifier of the end-user.
         * userAttributes : user principal
         */ 
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        String authProvider = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        processOAuth2User(authProvider, oAuth2User);
        return new DefaultOAuth2User(singletonList(new SimpleGrantedAuthority("USER")), 
                                                                    oAuth2User.getAttributes(), 
                                                                    "name");
    }


    /* using email to find the user */
    private void processOAuth2User(String authProvider, OAuth2User oAuth2User){
        // mapper a userInfo 
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(authProvider, oAuth2User.getAttributes());
        
        // check if mail is null
        if(userInfo.getEmail().isBlank()) {
            throw new OAuth2UserInfoAuthenticationException("Email not found from OAuth2AuthenticationManagerProvider");
        }

        /*  update the local database
            https://stackoverflow.com/questions/53039013/java-9-ifpresentorelse-returning-value/53039364
         */
        User thisUser = userRepo.findByMail(userInfo.getEmail())
                           .map(user-> updateTheMember(user, userInfo))
                           .orElseGet(()->registerAnewMember(userInfo));
        log.info(thisUser.getUsername());
    }

    private User registerAnewMember(OAuth2UserInfo userInfo){
        User newMember = User.builder()
                            .authProvider(userInfo.getAuthProvider())
                            .username(userInfo.getUsername())
                            .createdDate(Instant.now())
                            .legit(true)
                            .build();
        return userRepo.save(newMember);
    }

    private User updateTheMember(User user, OAuth2UserInfo userInfo){
        user.setUsername(userInfo.getUsername());
        return userRepo.save(user);
    }
}
