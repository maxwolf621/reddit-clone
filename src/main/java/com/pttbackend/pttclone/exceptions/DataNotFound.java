package com.pttbackend.pttclone.exceptions;

public class DataNotFound extends RuntimeException {
    public DataNotFound(String message, Throwable cause){
        super(message, cause);
    }
    public DataNotFound(String message){
        super(message);
    }
}
