package com.pttbackend.pttclone.utility;

import lombok.experimental.UtilityClass;

/**
 * A page for activating User via Token
 */
@UtilityClass
public class GoTokenPage {
    
    private static final String TOKEN_PAGE = "http://localhost:4200/tokenVerify/";
    
    private static final String RESET_PASSWORD_PAGE = "http://localhost:4200/resetPassword/";

    public String tokenVerificationUrl(){
        return TOKEN_PAGE;
    }

    public String restPasswordUrl(){
        return RESET_PASSWORD_PAGE;
    }
}
