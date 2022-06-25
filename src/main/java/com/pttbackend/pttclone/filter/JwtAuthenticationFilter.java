package com.pttbackend.pttclone.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pttbackend.pttclone.security.JwtProvider;

import java.io.IOException;

/**
 * To read and verify JWT contained in request 
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * <p> read the jwt via   {@link JwtProvider#parserToken(String)} </p>
     * <p> verify the jwt via {@link JwtProvider#getUserNameFromToken(String)} </p>
     * @see org.springframework.security.authentication.UsernamePasswordAuthenticationToken#UsernamePasswordAuthenticationToken(Object, Object, java.util.Collection)
     * @see UsernamePasswordAuthenticationToken#setDetails(Object)
     * @see SecurityContextHolder#getContext()
     * @see SecurityContext#setAuthentication(org.springframework.security.core.Authentication)
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String jwt = getJwtFromRequest(request);
        
        if (StringUtils.hasText(jwt) && jwtProvider.parserToken(jwt)) {
                log.info("'-------------------------------Parse the JWT");
                String username = jwtProvider.getUserNameFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                        null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        filterChain.doFilter(request, response);
    }

    /**
     * get JWT from header 
     * @param request from client contained JWT
     * @see HttpServletRequest#getHeader(String)
     * @see StringUtils#hasText(CharSequence)
     * @return String jwt
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        log.info("get Jwt from Request's header");
        String bearerToken = request.getHeader("Authorization");
        log.info("{ Authorization : " + bearerToken + " }");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        log.info("The jwt In this request is invalid ");
        return bearerToken;
    }
}
