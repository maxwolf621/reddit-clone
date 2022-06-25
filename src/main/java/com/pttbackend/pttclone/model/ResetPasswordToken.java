package com.pttbackend.pttclone.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import static javax.persistence.FetchType.EAGER;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.AUTO;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "resetpassword_token")
public class ResetPasswordToken {
     
    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name="reset_password_id")
    private Long id;
 
    @Column(name = "reset_password_token")
    private String token;
 

    @OneToOne(targetEntity = User.class, fetch = EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;
 
    // Hwo long this token will exist
    @Column(name = "expiry_date")
    private Date expiryDate;
}
