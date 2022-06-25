package com.pttbackend.pttclone.dto;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    // authenticationToken jwt 
    private String authenticationToken;
    private String username;
}