package com.pttbackend.pttclone.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import com.pttbackend.pttclone.exceptions.TokenException;
import com.pttbackend.pttclone.model.RefreshToken;
import com.pttbackend.pttclone.repository.RefreshTokenRepository;
import com.pttbackend.pttclone.security.JwtProvider;


/**
 * <p> Service for Refreshing the Token if the user's token expired  </p>
 * <p> Used by {@link JwtProvider} and {@link AuthenticationService} </p>
 */
@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepo;

    /** 
     * Generate Refresh Token via {@code UUID.randomUUID().toString()}
     * @return {@link RefreshToken}
     * @see java.util.UUID#randomUUID()
     */
    public RefreshToken generateRefreshToken() {
        RefreshToken refreshToken = RefreshToken.builder()
                                                .token(UUID.randomUUID().toString())
                                                .createdDate(Instant.now())
                                                .build();
                                            
        log.info("Saving New RefreshToken");
        return refreshTokenRepo.save(refreshToken);
    }

    
    /** 
     * Find The Token stored in RefreshToken Repository
     * @param token might store in {@link RefreshToken}
     */
    void validateRefreshToken(String token) {
        RefreshToken refreshToken= refreshTokenRepo.findByToken(token).orElseThrow(
            () -> new TokenException("Invalid Refresh Token" + token)
        );
        
        log.info("RefreshToken " + refreshToken.getToken() + "is validate");
    }

    
    /** 
     * Delete The Token stored in RefreshTOken Repository
     * @param token in {@link RefreshToken}
     */
    public void deleteRefreshToken(String token) {
        refreshTokenRepo.deleteByToken(token);
        log.info("The Token is deleted");
    }
}

