package com.example.exceptions;

public class UsernameExistsException extends ResourceConflictException{

    public UsernameExistsException(String username) {
        super("The username " + username + " already exists.", "USERNAME_ALREADY_EXISTS");
    }
}
