package com.pttbackend.pttclone.service;

import java.util.Collection;


import com.pttbackend.pttclone.model.User;
import com.pttbackend.pttclone.repository.UserRepository;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.util.Collections.singletonList;


/** 
 * <p> UserDetailsService For LOCAL Log-in </p>
 * <p> via {@link #loadUserByUsername(String)} 
 * @see <a href="https://stackoverflow.com/questions/49715769/why-is-my-oauth2-config-not-using-my-custom-userservice">
 *      oauth2-custom-UserService</a>
 */
@Service
@AllArgsConstructor
@Slf4j
public class UserPrincipalService implements UserDetailsService{

    private final UserRepository userRepo;

    /**
     * <p> For Custom Provider to fetch User Details (UserRepository) </p> 
     * @return {@code UserDetails} of Principal
     * @see <a href="https://github.com/spring-projects/spring-security/blob/main/core/src/main/java/org/springframework/security/core/userdetails/User.java">
     *      org.springframework.security.core.userdetails.User</a>
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        User user = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User :" + username +" Not Found")); 
        
        /**
         * userdetails.User(String username, 
         *                  String password, 
         *                  boolean enabled, 
         *                  boolean accountNonExpired,
         *                  boolean credentialsNonExpired, 
         *                  boolean accountNonLocked,
         *                  Collection<? extends GrantedAuthority> authorities)
         */

        log.info("Get Authenticated User Details");
        return new org.springframework.security.core.userdetails.User(
                                        user.getUsername(), 
                                        user.getPassword(),
                                        user.isLegit(), 
                                        true,
                                        true,
                                        true, 
                                        this.getAuthorities("USER")
                                        );
    }
    
    /**
     * @param role Log-In USER's role
     * @return an immutable list containing only the specified object
     * @see <a href="https://github.com/openjdk-mirror/jdk7u-jdk/blob/master/src/share/classes/java/util/Collections.java#L3348">
     *      java.util.Collections#singletonList(Object)</a>
     * @see <a href="https://github.com/spring-projects/spring-security/blob/main/core/src/main/java/org/springframework/security/core/authority/SimpleGrantedAuthority.java#L38">
     *      SimpleGrantedAuthority#SimpleGrantedAuthority(String)</a>
     */
    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return singletonList(new SimpleGrantedAuthority(role));
    }

}
