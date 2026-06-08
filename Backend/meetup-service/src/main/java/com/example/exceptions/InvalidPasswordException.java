package com.example.exceptions;

public class InvalidPasswordException extends InvalidCredentialsException{
    public InvalidPasswordException() {
        super("INVALID_PASSWORD", "The password you have entered is incorrect");
    }
}
