package com.pttbackend.pttclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    /**
     * {@code @JsonIgnore} 
     * is used at field level to mark a property 
     * or list of properties to be ignored.
     */
    private String username;
    private String password;   
}
