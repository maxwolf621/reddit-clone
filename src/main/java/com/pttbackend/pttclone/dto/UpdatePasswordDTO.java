package com.pttbackend.pttclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Situation : ForgetPassword or UpdatePassword
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordDTO {
    private String oldPassword;
    private String newPassword;

    // token to verify for the user who forgets her/his own password 
    private String resetPasswordToken;
}
