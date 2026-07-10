package com.example.meetup_service;

import java.time.LocalDate;
import java.time.LocalTime;


//we put this as the return type in findNearbyMeetups() method in Meetup repo
//spring auto maps the data thats received as lat, lng, meetupName ... to the interface
public interface MeetupResponseProjection {
    Double getLat();
    Double getLng();
    String getMeetupName();
    LocalDate getMeetupDate();
    LocalTime getMeetupTime();
    Double getDistance();
}