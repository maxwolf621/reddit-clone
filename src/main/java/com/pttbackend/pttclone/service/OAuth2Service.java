package com.pttbackend.pttclone.service;

import com.pttbackend.pttclone.model.User;
import com.pttbackend.pttclone.oauth2userinfo.OAuth2UserInfo;
import com.pttbackend.pttclone.repository.UserRepository;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

/**
 * <p> 
 * A Service for Oauth2 User to register 
 * or update the account for this application 
 * </p>
 */
@Service
@AllArgsConstructor
@Slf4j
public class OAuth2Service {
    
    // User Repository
    private UserRepository userRepo;

    /**
     * Process {@code #update(User, OAuth2UserInfo)} or {@code #register(OAuth2UserInfo)}
     * @param userInfo {@link OAuth2UserInfo}
     * @param email {@link User}'s mail
     */
    public void processOauth2User(OAuth2UserInfo userInfo, String email){
        log.info("      '--- Process Oauth2 User");
        User thisUser = userRepo.findByMail(email)
                                .map(user-> update(user, userInfo))
                                .orElseGet(()->register(userInfo));
        log.info("          '--- Show Member Name:"+thisUser.getUsername());
    }

    /**
     * Create a new user via user information 
     * from thrid party account e.g. GITHUB ... etc 
     * @param userInfo containing attributes from third party
     * @return {@link User} 
     */
    public User register(OAuth2UserInfo userInfo){
        log.info("  '--- Register a new Member");
        User newMember = User.builder()
                             .mail(userInfo.getEmail())
                             .authProvider(userInfo.getAuthProvider())
                             .username(userInfo.getUsername())
                             .createdDate(Instant.now())
                             .legit(true)
                             .build();
        return userRepo.save(newMember);
    }

    /**
     * Update the user information via third party account
     * @param existingUser Check if the user 
     *                     is existing or not in user Repository
     * @param userInfo Attributes from third party user account
     * @return {@link User}
     */
    public User update(User existingUser, OAuth2UserInfo userInfo){
        log.info("  '--- Update The Member Data From the Third Party Application");
        existingUser.setUsername(userInfo.getUsername());
        return userRepo.save(existingUser);
    }
    
}
