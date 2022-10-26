package com.pttbackend.pttclone.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

/**
 * A user's verification token
 * @see <a href="https://stackoverflow.com/questions/61062003/how-to-check-if-token-expired-in-java">
 *      reference[Expiry Token]</a>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "verificationtoken")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "token_id", unique = true,nullable = false)
    private Long id;
    
    @Column(name = "token")
    private String token;
    
    @OneToOne(fetch = LAZY)
    private User user;
    
    @Column(name = "expiryDate")
    private Date expiryDate;

    /*
    @javax.persistence.Transient
    public boolean isTokenExpired() { return ! Instant.now().isBefore(expiryDate);}
    */
}
