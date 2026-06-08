package com.example.user_service;

import jakarta.validation.constraints.NotNull;

public class SignupDTO {
    @NotNull(message = "Username cannot be null")
    private String username;
    @NotNull(message = "Email cannot be null")
    private String email;
    @NotNull(message = "Password cannot be null")
    private String password;

    public SignupDTO(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        System.out.println(password);
    }

    //Getters & Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
