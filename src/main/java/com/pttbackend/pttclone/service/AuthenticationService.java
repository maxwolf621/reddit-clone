package com.pttbackend.pttclone.service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import com.pttbackend.pttclone.dto.LoginRequest;
import com.pttbackend.pttclone.dto.RefreshTokenRequest;
import com.pttbackend.pttclone.dto.RefreshTokenResponse;
import com.pttbackend.pttclone.dto.RegisterRequest;
import com.pttbackend.pttclone.dto.UpdatePasswordDTO;
import com.pttbackend.pttclone.exceptions.DataNotFound;
import com.pttbackend.pttclone.exceptions.TokenException;
import com.pttbackend.pttclone.model.AuthProviderType;
import com.pttbackend.pttclone.model.NotificationMail;
import com.pttbackend.pttclone.model.User;
import com.pttbackend.pttclone.model.VerificationToken;
import com.pttbackend.pttclone.repository.UserRepository;
import com.pttbackend.pttclone.repository.VerificationTokenRepository;
import com.pttbackend.pttclone.security.JwtProvider;
import com.pttbackend.pttclone.security.OAuth2UserPrincipal;
import com.pttbackend.pttclone.utility.GoTokenPage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthenticationService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepo;
    private final SendMailService sendmailService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @Value("${jwt.expiration.time}")
    private Long tokenExpirationInMillis;
    
    /**
     * Encrypt the password of the user
     * @param password {@link User}'s Password
     * @return String 
     * @see org.springframework.security.crypto.password.PasswordEncoder
     */
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Create a User 
     * @param req {@link RegisterRequest}
     * @see SendMailService
     */
    public void signup(RegisterRequest req){
        log.info("---Sing Up Process");
        User newUser = User.builder()
                           .username(req.getUsername())
                           .password(encodePassword(req.getPassword()))
                           .mail(req.getMail())
                           .legit(false)
                           .authProvider(AuthProviderType.LOCAL)
                           .createdDate(Instant.now())
                           .build();
        
        userRepo.save(newUser); 
        log.info("__ Save a new user successfully");
        
        String token = this.generateToken(newUser); 
        /**
         * Send mail to user to activate account 
         * 
         * <pre> 
         * String body = ("click the URL To activate Your Account : " + GoTokenPage.url() + token); 
         * String subject = "Activate Your Account";
         * String recipient = newUser.getMail(); 
         * </pre>
        */
        NotificationMail activateMail = NotificationMail.builder()
                                                        .subject("Activate Your Account")
                                                        .body("Yo Click the link below To activate Your Account : \n" + GoTokenPage.tokenVerificationUrl() + token)
                                                        .recipient(newUser.getMail())
                                                        .build();
        sendmailService.sendTokenMail(activateMail);
        log.info("---- Account Activate Token Haven Been Sent");
    }

    
    /** 
     * <p> Called by {@code #signup(RegisterRequest)} to verify the new user account </p>
     * <p> Called by {@code UserController#resetPassword(String)} to reset the password of the user </p>
     * @param user {@link User}
     * @return String
     */
    private String generateToken(User user){
            String token = UUID.randomUUID().toString();
            verificationTokenRepo.findByUser(user).ifPresentOrElse(
                existToken -> this.createResetPasswordToken(existToken, token),
                ()-> this.createVerificationTokenForUser(user,token)
            );
            return token;
    }

    /**
     * 
     * Create ResetPassword Token for user to reset their Password
     * @param verificationToken {@link VerificationToken}
     * @param token Generated New Token for reset the password
     */
    private void createResetPasswordToken(VerificationToken verificationToken,String token){
        verificationToken.setToken(token);
        verificationToken.setExpiryDate(Date.from(Instant.now().plusMillis(tokenExpirationInMillis)));
        
        verificationTokenRepo.save(verificationToken);
    }

    /**
     * Create Verification Token to Verify The User Account
     * @param user the User haven't been activated by activate email
     * @param token a token that authenticated the user
     * @return String
     */
    private String createVerificationTokenForUser(User user,String token){
        VerificationToken verificationToken = VerificationToken.builder()
                                                               .token(token)
                                                               .user(user)
                                                               .expiryDate(Date.from(Instant.now().plusMillis(tokenExpirationInMillis)))
                                                               .build();
        log.info("The User :" 
                 + verificationToken.getUser().getUsername() 
                 + " has a verificationToken " 
                 + token 
                 + " Expire At "
                 + verificationToken.getExpiryDate()
        );
        verificationTokenRepo.save(verificationToken);
        return token;
    }


    /**
     * Send The Mail to User To Reset The Password
     * @param email {@link User}'s Mail
     */
    public void sendMailToResetPassword(String email){
        
        User user = userRepo.findByMail(email).orElseThrow(() -> new DataNotFound("Email Not Found"));
        
        String token = this.generateToken(user);
        
        NotificationMail resetTokenMail = NotificationMail.builder()
                                                          .subject("Link To Reset Your Password")
                                                          .body("click the URL To Reset Your Password : " + GoTokenPage.restPasswordUrl() + token)
                                                          .recipient(email)
                                                          .build();
        sendmailService.sendTokenMail(resetTokenMail);
    }

    /**
     * resetPassword (for user who forgets the password)
     * via ResetPasswordToken to reset the Password
     * @param updatePasswordDTO {@link UpdatePasswordDTO}
     */
    public void resetPassword(UpdatePasswordDTO updatePasswordDTO){
        VerificationToken verificationToken = verificationTokenRepo.findByToken(updatePasswordDTO.getResetPasswordToken())
                                                                   .orElseThrow(() -> new TokenException("Invalid Verification Token"));
        /*
        if(verificationToken.isTokenExpired()){
            throw new TokenException("The Token you gave for reset password has expired ");
        }
        */

        User user = userRepo.findByUsername(verificationToken.getUser().getUsername()).orElseThrow(()-> new UsernameNotFoundException("User Not Found"));
        user.setPassword(this.encodePassword(updatePasswordDTO.getNewPassword()));
        userRepo.save(user);
    }
    
     /**
      *  
      * It is called once the user access the validating token page
      * @param token {@link VerificationToken}'s Token
      */
     public void verifyToken(String token){
        /**
         VerificationToken verificationToken = 
            verificationTokenRepo.findByToken(token).orElseThrow(() -> new TokenException("Invalid Verification Token"+ token ));
        */
        verificationTokenRepo.findByToken(token)
                .ifPresentOrElse(
                    this::setUserValid,
                    () -> new TokenException("Invalid Verification Token"+ token )
                );
    }
     

    /**
     * Called by {@code #verifyToken(String)}
     * Set The User as Valid User 
     * dependent on verificationToken provided by User   
     * @param verificationToken {@link VerificationToken}
     */
    private void setUserValid(VerificationToken verificationToken){

        /*
        if(verificationToken.isTokenExpired()){
            throw new TokenException("The Token you gave for activate account has expired");
        }
        */

        String username = verificationToken.getUser().getUsername();
        User user = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User NOT Found " + username));
        
        user.setLegit(true);
        userRepo.save(user);
        log.info("--- Saving A Legitimate User Successfully");
    }

    /**
     * Login
     * @param loginRequest DTO {@link LoginRequest}
     * @return {@link RefreshTokenResponse}
     */
    public RefreshTokenResponse login(LoginRequest loginRequest) {
        log.info("'**** Login Process");
        
        // Authentication Process (security configuration)
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                                        loginRequest.getUsername(),loginRequest.getPassword()));
        // Authenticated this logged in User 
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        // Generate jwt   
        String jwt = jwtProvider.TokenBuilderByUser(authenticate);
        return RefreshTokenResponse.builder()
                                   .token(jwt)
                                   .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                                   //.expiresAt(Instant.now())
                                   .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                                   .username(loginRequest.getUsername())
                                   .build();
    }

    /**
     * If JWT has expired then {@link RefreshTokenService} 
     * validates the RefreshToken given by Client {@link JwtProvider} generates a new jwt 
     * @param refreshTokenRequest {@link RefreshTokenRequest}
     * @return {@link RefreshTokenResponse}
     */
    public RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        log.info("**** Refresh Token Process");
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String jwt = jwtProvider.TokenBuilderByUserName(refreshTokenRequest.getUsername());
        log.info("--Generate A RefreshToken : " + jwt);
        return RefreshTokenResponse.builder()
                                   .token(jwt)
                                   .refreshToken(refreshTokenRequest.getRefreshToken())
                                   .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                                   .username(refreshTokenRequest.getUsername())
                                   .build();
    }


    /**
     * Get Current Logged In User
     * @return {@link User}
     */
    @Transactional(readOnly = true)
    public User getCurrentUser(){
        log.info("Get Current(logged) User");
        try{
            org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = principal.getUsername();
            return userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("The user your search" + username +"doesn't exist"));

        }catch(java.lang.ClassCastException e){
            log.warn("** Class Case Exception" + e);
            OAuth2User oAuth2User = (OAuth2UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = oAuth2User.getName();
            return userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("The user your search" + username +"doesn't exist"));
        }
    }

    /**     
     * {@code SecurityContextHolder.getContext()#getAuthentication()} 
     * to get current user detail (principal)
     * via {@code Authentication#isAuthenticated()}
     * @return boolean
     */
    public boolean isUserLoggedIn() {
        //log.info("Check if User is Logged In");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }

    
    public void deleteUser(String email){
        userRepo.deleteByMail(email);
    }

    @Scheduled(cron = "${purge.cron.expression}")
    public void deleteExpiredToken(){
        // delete Expired Token
        verificationTokenRepo.deleteExpiredToken(Date.from(Instant.now()));
        // delete User
        userRepo.deleteIllegitimateUser();
    }
    
}
