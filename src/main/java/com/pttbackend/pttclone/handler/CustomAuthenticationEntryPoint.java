package com.pttbackend.pttclone.handler;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p> For unauthenticated user trying to access protected resource </p>
 */
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    /**
     * @see org.springframework.security.web.AuthenticationEntryPoint
     */
    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException authenticationException) throws IOException, ServletException {
        log.error("Responding with unauthorized error. Message - {}", authenticationException.getMessage());
        
        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, authenticationException.getLocalizedMessage());
    }
}
