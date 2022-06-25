package com.pttbackend.pttclone.config;

import com.pttbackend.pttclone.handler.CustomAuthenticationEntryPoint;
import com.pttbackend.pttclone.handler.OAuth2USerAuthenticationFailureHandler;
import com.pttbackend.pttclone.handler.OAuth2UserAuthenticationSuccessHandler;
import com.pttbackend.pttclone.repository.CustomOAuth2AuthorizationRequestRepository;
import com.pttbackend.pttclone.service.CustomOAuth2UserPrincipalService;
import com.pttbackend.pttclone.service.UserPrincipalService;
import com.pttbackend.pttclone.filter.JwtAuthenticationFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * <p> Spring Security Configuration </p>
 * @see <a href="https://developer.aliyun.com/article/496751"> Spring security example 1</a>
 * @see <a href="https://www.cnblogs.com/Java-125/p/9012461.html">Spring security example 2</a>
 * @see <a href="https://pjchender.dev/internet/note-oauth2/">Spring security example 3</a>
 * @see <a href="https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/oauth2/client/web/HttpSessionOAuth2AuthorizationRequestRepository.html">
 *      HttpSessionOAuth2AuthorizationRequestRepository </a>
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
@AllArgsConstructor
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    private final CustomOAuth2AuthorizationRequestRepository authorizationRequestRepository;
    private final OAuth2UserAuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2USerAuthenticationFailureHandler oAuth2USerAuthenticationFailureHandler;
    private final CustomOAuth2UserPrincipalService customOAuth2UserPrincipalUserService;

    private final UserDetailsService userdetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * <a href="https://stackoverflow.com/questions/37671125/how-to-configure-spring-security-to-allow-swagger-url-to-be-accessed-without-aut">
     * Paths of Swagger Setting</a>
     */
    private static final String[] AUTH_WHITELIST = {
        // -- Swagger UI v2
        "/v2/api-docs",
        "/swagger-resources",
        "/swagger-resources/**",
        "/configuration/ui",
        "/configuration/security",
        "/swagger-ui.html",
        "/webjars/**",
        // -- Swagger UI v3 (OpenAPI)
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/",
        "/error",
        "/favicon.ico",
        "/**/*.png",
        "/**/*.gif",
        "/**/*.svg",
        "/**/*.jpg",
        "/**/*.html",
        "/**/*.css",
        "/**/*.js"
    };

    /**
     * <p> To configure our http index/directory </p>
     * <p> cors, session management, csrf </p>
     * <p> exceptionHandling for authenticationEntryPoint </p>
     * <p> Authorization for each page </p>
     * <p> Oauth2's authorizationEndpoint, redirectionEndpoint, userInfoEndpoint, successHandler and failureHandler </p>
     * <p> Set up a custom filter via {@code addFilterBefore} for JWT interceptor  </p>
     * @param http security Our http directory
     */
    @Override
    public void configure(HttpSecurity http) throws Exception{
        http
            .cors()
                .and()
            .csrf().disable()
            //.formLogin().disable()
            .httpBasic().disable()
            .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .and()
            //.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests()
                .antMatchers("/api/auth/**", "/oauth2/**").permitAll()
                .antMatchers("/api/post","/api/sub", "/api/tag").permitAll()
                .antMatchers(HttpMethod.GET, "/api/post/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/comment/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/tag/**").permitAll()
                .antMatchers(AUTH_WHITELIST).permitAll() 
                .anyRequest().authenticated()
                .and()
            .oauth2Login()
                .authorizationEndpoint()        
                    .baseUri("/oauth2/authorization")
                    .authorizationRequestRepository(authorizationRequestRepository)
                    .and()
                .redirectionEndpoint()         
                    .baseUri("/oauth2/**")
                    .and()
                .userInfoEndpoint() 
                    .userService(customOAuth2UserPrincipalUserService)
                    .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2USerAuthenticationFailureHandler);

        log.info("---------Filter For Local Login");
        http.addFilterBefore(jwtAuthenticationFilter,UsernamePasswordAuthenticationFilter.class);
    }

    /** 
    @Override
    public void configure(WebSecurity webSecurity) throws Exception{
    webSecurity.ignoring().antMatchers("/error/**");
    }
    */

    /**
     * Bean For A Custom Oauth2User Authorization Repository
     * @return {@code AuthorizationRequestRepository<OAuth2AuthorizationRequest>}
     */
    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> customAuthorizationRequestRepository() {
	    return new CustomOAuth2AuthorizationRequestRepository();
    }

    /**
     *  <p> Bean For Password Encoder </p>
     *  <p> This method is used to set up </p>
     *  <p> {@link AuthenticationManagerBuilder#userDetailsService(UserDetailsService)}
     *      via {@link BCryptPasswordEncoder#BCryptPasswordEncoder()}
     *  </p>
     * @return {@code org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptPasswordEncoder()}
     */
    @Bean
    PasswordEncoder passwordEncoder(){
            return new BCryptPasswordEncoder();
    }

    /**
     * <p> Bean for a custom AuthenticationManager
     *     that can provide a custom AuthenticationProvider 
     *     {@link #configureCustomProvider(AuthenticationManagerBuilder)}
     *     and expose it as a bean (IoC) {@code @Bean(BeanIds.AUTHENTICATION_MANAGER)} </p>
     */
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * <strong> Configure custom Authentication Provider </strong>
     * <p> {@link UserPrincipalService} : to load the authentication user </p>
     * @param buildauthenticationprovider 
     * <pre> AuthenticationManagerBuilder#userDetailsService(UserDetailsService)} // To build a custom User Detail </pre> 
     * <pre> AbstractDaoAuthenticationConfigurer#passwordEncoder(PasswordEncoder) // To build password encoder </pre>
     * @throws Exception buildauthenticationprovider failed
     */
    @Autowired
    public void configureCustomProvider(AuthenticationManagerBuilder buildauthenticationprovider) throws Exception{
        buildauthenticationprovider.userDetailsService(userdetailsService).passwordEncoder(passwordEncoder());
    }
}
