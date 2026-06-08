package com.example.exceptions;

public class ResourceConflictException extends RuntimeException{
    private String errorCode;

    public ResourceConflictException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
