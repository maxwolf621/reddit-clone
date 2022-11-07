package com.pttbackend.pttclone.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pttbackend.pttclone.repository.CustomOAuth2AuthorizationRequestRepository;
import com.pttbackend.pttclone.utility.CookieUtils;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import static com.pttbackend.pttclone.repository.CustomOAuth2AuthorizationRequestRepository.REDIRECT_URI_COOKIE;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <p> If log-in via third party failed then delete the cookies 
 *     and build a Uri with error information and query parameter in it </p>
 * @see CookieUtils#getCookie(HttpServletRequest, String)
 * @see CustomOAuth2AuthorizationRequestRepository#removeAuthorizationRequestCookies(HttpServletRequest, HttpServletResponse)
 * @see org.springframework.web.util.UriComponentsBuilder#fromUriString(String)
 * @see org.springframework.security.web.RedirectStrategy#sendRedirect(HttpServletRequest, HttpServletResponse, String)
 */
@Component
@Slf4j
@AllArgsConstructor
public class OAuth2USerAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler{
    private final CustomOAuth2AuthorizationRequestRepository customOAuth2AuthorizationRequestRepo;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, 
                                        HttpServletResponse response, 
                                        AuthenticationException exception) 
                                        throws IOException, ServletException {
        // get redirectURI from cookie
        String redirectURI = CookieUtils
                                .getCookie(request, REDIRECT_URI_COOKIE)
                                .map(Cookie::getValue)
                                .orElse(("/"));

        // redirect Uri with `error?=` query parameter in it
        redirectURI = UriComponentsBuilder
                        .fromUriString(redirectURI)
                        .queryParam("error", exception.getLocalizedMessage())
                        .build()
                        .toUriString();
        log.info("______________redirectURI______________: "+redirectURI);

        // remove cookies in http session
        customOAuth2AuthorizationRequestRepo.removeAuthorizationRequestCookies(request, response);        
        // redirect to url
        getRedirectStrategy().sendRedirect(request, response, redirectURI);
    }
}
