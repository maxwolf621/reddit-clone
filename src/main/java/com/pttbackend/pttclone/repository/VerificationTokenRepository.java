package com.pttbackend.pttclone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

import com.pttbackend.pttclone.model.User;
import com.pttbackend.pttclone.model.VerificationToken;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByUser(User user);

    @Modifying
    @Query("delete from VerificationToken v where v.expiryDate <= ?1")
    void deleteExpiredToken(Date now);

}
