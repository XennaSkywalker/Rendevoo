package com.example.exceptions;

public class EmailExistsException extends ResourceConflictException{

    public EmailExistsException(String email) {
        super("The email address " + email + " already exists.", "EMAIL_ALREADY_EXISTS");
    }
}
