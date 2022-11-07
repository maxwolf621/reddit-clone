package com.pttbackend.pttclone.repository;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pttbackend.pttclone.utility.CookieUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;


/**d
 * <p> Repository btw Client, 
 *     CustomOAuth2AuthorizationRequestRepository 
 *     and Authorization Provider(3rd party Application) </p>
 * @see <a href="https://inf.news/technique/9ad8deaa4c5136db9d7829491eb12ffb.html"> 
 *       Oauth2User Filter for Repository </a>
 */
@Repository
@Slf4j
public class CustomOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest>{
    
    // cookie name
    public static final String AUTHORIZATION_REQUEST_COOKIE = "oauth2_auth_request";
    // redirect_uri (after login)
    public static final String REDIRECT_URI_COOKIE = "redirect_uri";
    private static final int COOKIE_EXPIRES_SECONDS = 1800;

    /**
     * <p> Load Authorization Request <p>
     * @param request Compare the cookie from {@link HttpServletRequest} request with Authorization Provider's cookie 
     * @return {@link org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest} (from Servlet) 
     * associated to the provided HttpServletRequest (from Client) 
     * or {@code null} if not available. 
    */
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        log.info("-------------loadAuthorizationRequest from HttpServletRequest-------------");
        log.info(request.getRequestURL().toString());

        Assert.notNull(request, "request cannot be null");

        return CookieUtils.getCookie(request, AUTHORIZATION_REQUEST_COOKIE)
                .map(cookie -> CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    /**
     * <p> saveAuthorizationRequest(cookies) in the client's session</p>
     * @param authorizationRequest {@link OAuth2AuthorizationRequest}
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     */
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        log.info("-------------Start of saveAuthorizationRequest-------------");

        Assert.notNull(request, "request cannot be null");
		Assert.notNull(response, "response cannot be null");
        
        if (authorizationRequest == null) {
            // delete the cookies stored in the client session
            CookieUtils.deleteCookie(request, response, AUTHORIZATION_REQUEST_COOKIE);
            CookieUtils.deleteCookie(request, response, REDIRECT_URI_COOKIE);
            return;
        }   

        // add AUTHORIZATION_REQUEST_COOKIE & REDIRECT_URI_COOKIE information to response
        // the response will be retrieved by client
        log.info("___Add authorization-request cookie");
        CookieUtils.addCookie(response, AUTHORIZATION_REQUEST_COOKIE, 
                              CookieUtils.serialize(authorizationRequest), 
                              COOKIE_EXPIRES_SECONDS);
        
        String redirectUriAfterLogin = request.getParameter(REDIRECT_URI_COOKIE);        
        if (StringUtils.isNotBlank(redirectUriAfterLogin)) {
            log.info("___Add redirect_uri Cookie");
            CookieUtils.addCookie(response, REDIRECT_URI_COOKIE, 
                                  redirectUriAfterLogin, 
                                  COOKIE_EXPIRES_SECONDS);
        }

        log.info("-------------End Of saveAuthorizationRequest-------------");
    }

    /** 
     * Used by OAuth2UserLoginAuthentication filter
     * <p> delete the cookies in the http session via 
     *     {@code #removeAuthorizationRequestCookies(HttpServletRequest, HttpServletResponse)} </p>
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     */
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return this.loadAuthorizationRequest(request);
    }

    @Deprecated
	@Override
	public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
		throw new UnsupportedOperationException("Spring Security shouldn't have called the deprecated removeAuthorizationRequest(request)");
	}

    /**
     * <p> Remove the cookies {@code AUTHORIZATION_REQUEST_COOKIE} and {@code REDIRECT_URI_COOKIE} 
     *     stored in client's session/browser </p> 
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @see CookieUtils#deleteCookie(HttpServletRequest, HttpServletResponse, String)
     */ 
    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response){
        log.info("-------------Remove the Cookies in HTTP the session-------------");
        CookieUtils.deleteCookie(request, response, AUTHORIZATION_REQUEST_COOKIE);
        CookieUtils.deleteCookie(request, response, REDIRECT_URI_COOKIE);
    }
}
