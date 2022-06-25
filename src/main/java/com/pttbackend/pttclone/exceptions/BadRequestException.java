package com.pttbackend.pttclone.exceptions;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.springframework.http.HttpStatus;
/**
 * Exception For HttpStatus BAD REQUEST
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public BadRequestException(String message) {
        super(message);
    }
}
