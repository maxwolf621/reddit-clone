package com.pttbackend.pttclone.exceptions;

public class JwtProviderException extends RuntimeException {

    public JwtProviderException(String message, Throwable cause){
        super(message,cause);
    }
    
    public JwtProviderException(String message){
        super(message);
    }
}
