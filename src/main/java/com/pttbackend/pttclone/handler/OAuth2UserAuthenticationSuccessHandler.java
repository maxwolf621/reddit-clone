package com.pttbackend.pttclone.handler;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pttbackend.pttclone.config.OAuth2Properties;
import com.pttbackend.pttclone.exceptions.BadRequestException;
import com.pttbackend.pttclone.oauth2userinfo.OAuth2UserInfo;
import com.pttbackend.pttclone.repository.CustomOAuth2AuthorizationRequestRepository;
import com.pttbackend.pttclone.security.JwtProvider;
import com.pttbackend.pttclone.security.OAuth2UserPrincipal;
import com.pttbackend.pttclone.service.OAuth2Service;
import com.pttbackend.pttclone.utility.CookieUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.pttbackend.pttclone.repository.CustomOAuth2AuthorizationRequestRepository.REDIRECT_URI_COOKIE;

/** 
 * <p> Handle the redirect URI with query parameter JWT on it</p>
 * @see <a href="https://github.com/spring-projects/spring-security/blob/main/web/src/main/java/org/springframework/security/web/authentication/SimpleUrlAuthenticationSuccessHandler.java">
 *      SimpleUrlAuthenticationSuccessHandler Specification</a>
 * @see <a href="https://spring.io/guides/gs/consuming-rest/">
 *      RestTemplate Example_1</a>
 * @see <a href="https://howtodoinjava.com/spring-boot2/resttemplate/spring-restful-client-resttemplate-example/">
 *      RestTemplate Example_2</a> 
 * @see <a href="https://spring.io/blog/2018/03/06/using-spring-security-5-to-integrate-with-oauth-2-secured-services-such-as-facebook-and-github">
 *      How to access API api resource</a> : 
 *      {@code Authentication} Instance kept in the security context 
 *      is actually an {@code OAuth2AuthenticationToken} which, 
 *      along with help from {@code OAuth2AuthorizedClientService} can avail us 
 *      with an access token for making requests against the service’s API.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{

    private final CustomOAuth2AuthorizationRequestRepository customOAuth2AuthorizationRequestRepository;
    private final JwtProvider jwtProvider; 
    private final OAuth2Properties oAuth2Properties;
    private final OAuth2Service oAuth2Service;
    private final OAuth2AuthorizedClientService authorizedClientService;

    /**
     * Fetch user email of GITHUB
     */
    @Value("${github.resource.userInfoUri}")
    private String userInfoUri; 
    
    /**
     * String name Github. 
     * {@value #GITHUB}
     */
    private static final String GITHUB = "Github";

    /**
     * <p> To generate JWT and build a new redirect_url 
     *     with jwt on it </p>
     * @param request  HttpServletRequest from user
     * @param response HttpServletResponse from server
     * @param authentication Authenticated User
     * @see  org.springframework.security.core.Authentication
     * @see  #determineTargetUrl(HttpServletRequest, HttpServletResponse, Authentication)
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        
        String targetUrl = determineTargetUrl(request, response, authentication);
        
        log.info("---------------Execute Success Handler ---------------");
        
        // A committed response has already had its status code and headers written.
        if (response.isCommitted()) {
            logger.debug("Client Already Received The Response. Unable redirect to " + targetUrl);
            return;
        }

        // Oauth2UserPrincipal object authenticated from CustomOauth2UserPrincipalService
        OAuth2UserPrincipal userPrincipal = (OAuth2UserPrincipal) authentication.getPrincipal();
        
        OAuth2UserInfo userInfo = userPrincipal.getUserInfo();
        String email =  userInfo.getEmail();
        
        log.info("   '-------Get Mail: " + email);
        
        /**
         * <p> Github does not give away user personal email. </p>
         * <p> Manually fetch user's mail via {@code GitHubTemplate} </p>
         */
        if(userInfo.getAuthProvider().toString().equalsIgnoreCase(GITHUB)){
            log.info("      '---- fetch github private mail");
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            log.info("          '____oauthToken: " + oauthToken.getName());
            OAuth2AuthorizedClient authorizedClient = 
                this.authorizedClientService.loadAuthorizedClient("github", oauthToken.getName());
            log.info("          '___authorizedClient: " + authorizedClient.getPrincipalName());
            String token = authorizedClient.getAccessToken().getTokenValue();
            log.info("          '____TOKEN: " + token);
            GitHubTemplate github = new GitHubTemplate(token);
            LinkedHashMap<String, Object>[] emails = github.getRestTemplate().getForObject(userInfoUri + "/emails", LinkedHashMap[].class);
            
            try{
                email = (String) emails[0].get("email");
                log.info("          '____Private Email: " + email );
            }catch(Exception e){
                log.warn(e.toString());
            }
        }

        // Assert {@code email} if it's null or not
        Assert.notNull(email, "Email Cant Not Be Null");
        
        //Update or Register user account
        oAuth2Service.processOauth2User(userInfo, email);
        
        // Delete cookie
        clearAuthenticationAttributes(request, response);
        

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * <p> this method fetches the redirect_url  
     *     from cookie contained in client request
     *     and provide a new redirect_url with JWT query parameter </p>
     * <p> Fetch Name-Value from Cookie {@link com.pttbackend.pttclone.utility.CookieUtils#getCookie(HttpServletRequest, String)} </p>
     * <p> Provide JWT {@link com.pttbackend.pttclone.security.JwtProvider#TokenBuilderByOauth2User(OAuth2UserPrincipal)} </p>
     * <p> Build Uri query parameter {@link org.springframework.web.util.UriComponentsBuilder#fromUriString(String)} </p>
     * <p> Remove the Cookie {@link com.pttbackend.pttclone.repository.CustomOAuth2AuthorizationRequestRepository#removeAuthorizationRequestCookies(HttpServletRequest,HttpServletResponse)} </p>
     * <p> Get Default URI {@link org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler#getDefaultTargetUrl()} </p>
     * @param request  HttpServletRequest from user
     * @param response HttpServletResponse from server
     * @param authentication Authenticated User
     *
     */
    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        
        log.info("Get Request path Info : " + request.getRequestURL());


        // get redirect uri from cookie
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_COOKIE).map(Cookie::getValue);


        // check if redirectUri is existing and is authorized 
        if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new BadRequestException("An Unauthorized Redirect URI : " + redirectUri.get());
        }
        
        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        // generate query parameters token and username
        OAuth2UserPrincipal principal = (OAuth2UserPrincipal) authentication.getPrincipal();
        String token = jwtProvider.TokenBuilderByOauth2User(principal);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .queryParam("username", principal.getName())
                .build().toUriString();
    }

    /**
     * <p> Delete related data that stored in server's session </p>
     * @param request  HttpServletRequest from user
     * @param response HttpServletResponse from server
     * @see org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler#clearAuthenticationAttributes(HttpServletRequest)
     * @see CustomOAuth2AuthorizationRequestRepository#removeAuthorizationRequestCookies(HttpServletRequest, HttpServletResponse)
     */ 
    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        customOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    /**
     * <p> Check URL from client's request is valid or not </p>
     * @param uri redirectUri from client's cookie
     * @return boolean
     * @see com.pttbackend.pttclone.config.OAuth2Properties
     * @see java.net.URI#create(String)
     * @see <a href="https://www.geeksforgeeks.org/stream-anymatch-java-examples/">
     *      stream().anyMatch() </a>
     */
    private boolean isAuthorizedRedirectUri(String uri) {
        
        /**
        public static URI create(String str) {
            try {
                    return new URI(str);
                } catch (URISyntaxException x) {
            throw new IllegalArgumentException(x.getMessage(), x);
            }
        }       
        */
        log.info("client's redirect URI is : " + uri);
        
        URI clientRedirectUri = URI.create(uri);
        
        // Check if client login via mobile or Desktop
        return oAuth2Properties.getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    
                    //log.info("clientRedirectUri.getHost() :" + authorizedURI.getHost() + " " + clientRedirectUri.getHost());
                    //log.info("clientRedirectUri.getPort() :" + authorizedURI.getPort() + " " + clientRedirectUri.getPort());

                    // check host and port
                    return (authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost()) && authorizedURI.getPort() == clientRedirectUri.getPort());
                    //return clientRedirectUri.equals(authorizedURI);
                });
    }
}
