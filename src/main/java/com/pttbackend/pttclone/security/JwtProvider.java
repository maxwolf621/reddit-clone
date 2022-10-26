package com.pttbackend.pttclone.security;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Instant;

import static java.util.Date.from;

import javax.annotation.PostConstruct;

import com.pttbackend.pttclone.exceptions.JwtProviderException;

import org.springframework.security.core.userdetails.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

/**
 * <p> Provide and read(parse) JWT </p>
 */
@Slf4j
@Service
public class JwtProvider {
    private KeyStore keyStore;

    @Value("${jwt.alias}")
    private String alias;
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration.time}")
    private Long jwtExpirationInMillis;


    /**
     * 
     * <p> Initialize The KeyStore Instance 
     *     Load the KeysStore that stored 
     *     in O.S into Class KeyStore's Instance </p>
     * @see KeyStore#getInstance(String)
     * @see KeyStore#load(InputStream, char[])
     * @see java.lang.Class#getResourceAsStream(String)
     */
    @PostConstruct
    public void init(){
            log.info("**************initialize the keystore******************");
            try {
                keyStore = KeyStore.getInstance("JKS");
                log.info("  '--- KeyStore.getInstance(\"JKS\")");
            } catch (KeyStoreException e) {
                e.printStackTrace();
                log.error("KeyStoreException" + e);
            }
            
            InputStream resourceAsStream = getClass().getResourceAsStream(alias);
            try {
                keyStore.load(resourceAsStream, getSecretKey().toCharArray());
                log.info("  '--- Load KeyStore from O.S into Class keystore instance");
            } catch (NoSuchAlgorithmException e) {
                log.error("Cryptographic Algorithm is not valid");
            } catch( CertificateException e){
                log.error("Certificate is not valid");
            } catch (java.io.IOException e) {
                log.error("IO Exceptions for InputStream");
            }
    }
    
    /** 
     * Create A jwt for authenticated user
     * @param authentication gets Principal and is casted to Userdetails.User 
     * @return the legitimate JWT to the user 
     * @see io.jsonwebtoken.Jwts
     */
    public String TokenBuilderByUser(Authentication authentication){
        log.info("** TokenBuilderByUser Generates the JWT for a Authenticated User");
        
        // Userdetails.User
        User principal = (User) authentication.getPrincipal();
        
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .setIssuedAt(from(Instant.now()))
                .signWith(getPrivateKey())
                .setExpiration(from(Instant.now().plusMillis(getJwtExpirationInMillis())))
                .compact();
    }       

    /**
     * Create A jwt for Oauth2User's redirect_url
     * @param principal contains user information
     * @return the legitimate JWT to Oauth2User
     *         by adding it in the redirect_url
     */
    public String TokenBuilderByOauth2User(OAuth2UserPrincipal principal){
        log.info("** TokenBuilderByOauth2User Generates the JWT for a Oauth2User");
        return Jwts.builder()
                .setSubject(principal.getName())
                .setIssuedAt(from(Instant.now()))
                .signWith(getPrivateKey())
                .setExpiration(from(Instant.now().plusMillis(getJwtExpirationInMillis())))
                .compact();
    }       

    /**
     * Create a jwt for username
     * @param username {@link User}'s name
     * @return {@code String} legitimate jwt
     */
    public String TokenBuilderByUserName(String username){ 
        log.info("** TokenBuilderByUserName Generates the JWT for a user's name");
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(from(Instant.now()))
                .signWith(getPrivateKey())
                //.setExpiration(LocalDate.ofInstant(Instant.now().plusMillis(getJwtExpirationInMillis()), ZoneId.systemDefault()))
                .setExpiration(from(Instant.now().plusMillis(getJwtExpirationInMillis())))
                .compact();
    }

    /**
     * Get A Private Key of KeyStore to sign a JWT
     * via {@link io.jsonwebtoken.JwtBuilder#signWith(java.security.Key)}
     * @return gerPrivate of keystore
     */
    private PrivateKey getPrivateKey(){
        log.info("** getPrivateKey of the instance `keystore` ");
        try {
            return (PrivateKey) keyStore.getKey("jwtoauth2", getSecretKey().toCharArray());
        } catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
            throw new JwtProviderException("Fetch Private Key Failed", e);
        }
    }

    /**
     * Public Key from the Certificate
     * @return {public key from this certificate jwtOauth2.}
     * @exception KeyStoreException generic KeyStore exception
     * @see java.security.cert.Certificate#getPublicKey()
     * @see java.security.KeyStore#getCertificate(String) 
     */
    private PublicKey getPublicKey() throws KeyStoreException{
        log.info("** getPublicKey from Certificate of the instance `keystore` ");
        return keyStore.getCertificate("jwtoauth2").getPublicKey();
    }

    /**
     * <p> Read A JWT </p> 
     * <p> It is used by {@link com.pttbackend.pttclone.filter.JwtAuthenticationFilter} </p>
     * @param token Jwt given by User to be parsed by Filter
     * @return {@code true} if token is valid  
     */
    public boolean parserToken(String token){
        log.info("*** Parse JWT");
        try {
            Jwts.parser()
                // check public key correspond to certificate
                .setSigningKey(getPublicKey())
                // check the token from the request payload
                .parseClaimsJws(token);
            log.info("** The JWT is valid");
            return true;
        } catch(ExpiredJwtException e){
            log.error("The jwt EXPIRED");
        } catch(SignatureException e){
            log.error("Signature Error" + e.toString());
        } catch(UnsupportedJwtException e){
            log.error("Jwt Form you provided isn't valid");
        } catch ( MalformedJwtException e){
            log.error("Jwt was not correctly constructed");
        } catch ( IllegalArgumentException e){
            log.error("Illegal Argument is passed");
        } catch (KeyStoreException e) {
            log.error("Key Store Error");
        }
        return false;
    }


    /**
     * Get User Name From Token via {@link io.jsonwebtoken.Claims#getSubject()}
     * @param token jwt given by client
     * @return the Username if not {@code null}
     */
    public String getUserNameFromToken(String token){
            log.info("** get UserName From Token");
            try {
                return Jwts.parser()
                        .setSigningKey(getPublicKey())
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject();
            }  catch(ExpiredJwtException e){
                log.error(" The jwt EXPIRED");
            } catch(SignatureException e){
                log.error("Signature Error" + e.toString());
            } catch(UnsupportedJwtException e){
                log.error("Jwt Form you provided isn't valid");
            } catch ( MalformedJwtException e){
                log.error("Jwt was not correctly constructed");
            } catch ( IllegalArgumentException e){
                log.error("Illegal Argument is passed");
            } catch (KeyStoreException e) {
                log.info("Key Store Error");
            }
            return null;
    }
    
    /**
     * @return JWT Expiration
     */
    public Long getJwtExpirationInMillis(){
        return jwtExpirationInMillis*10;
    }
    /**
     * @return Secret Key for this Keystore
     */
    public String getSecretKey(){
        return secretKey;
    }
}
