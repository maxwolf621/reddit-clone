package com.pttbackend.pttclone.exceptions;

public class TokenException extends RuntimeException {
    public TokenException(String message, Throwable cause){
        super(message, cause);
    }
    public TokenException(String message){
        super(message);
    }
}
