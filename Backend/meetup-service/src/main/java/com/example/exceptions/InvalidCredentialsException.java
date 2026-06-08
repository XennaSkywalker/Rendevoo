package com.example.exceptions;

public class InvalidCredentialsException extends RuntimeException{
    private String errorCode;

    public InvalidCredentialsException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
