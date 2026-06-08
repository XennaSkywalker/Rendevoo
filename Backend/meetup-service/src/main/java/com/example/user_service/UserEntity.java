package com.example.user_service;

import com.example.meetup_service.MeetupEntity;
import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class UserEntity implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String passwordHash;


    public UserEntity() {
    }

    public UserEntity(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

//    // JPA Hibernate doesnt automatically handle both sides of a bidirectional relationship
//    //if u do just meetups.add(...) but dont set the creator, in the DB table the value will be NULL
//    public void addMeetup(MeetupEntity meetup) {
//        this.meetups.add(meetup);
//        meetup.setCreator(this);
//    }
//
//
//    public void removeMeetup(MeetupEntity meetup) {
//        //similary here if u remove meetup from the user list, it wont automatically set the creator column in DB to null
//        this.meetups.remove(meetup);
//        meetup.setCreator(null);
//    }


    //Getters & Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public @Nullable String getPassword() {
        return passwordHash;
    }

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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

}
