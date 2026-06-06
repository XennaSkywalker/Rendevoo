package com.example.meetup_service;

import java.time.LocalDate;
import java.time.LocalTime;

public class MeetupRequestDTO {
    private double lat;
    private double lng;
    private String meetupName;
    private LocalDate meetupDate;
    private LocalTime meetupTime;

    @Override
    public String toString() {
        return "Meetup{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", meetupName='" + meetupName + '\'' +
                ", meetupDate=" + meetupDate +
                ", meetupTime=" + meetupTime +
                '}';
    }

    public MeetupRequestDTO() {
    }

    public MeetupRequestDTO(double lat, double lng, String meetupName, LocalDate meetupDate, LocalTime meetupTime) {
        this.lat = lat;
        this.lng = lng;
        this.meetupName = meetupName;
        this.meetupDate = meetupDate;
        this.meetupTime = meetupTime;
    }

    //Getters & Setters
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getMeetupName() {
        return meetupName;
    }

    public void setMeetupName(String meetupName) {
        this.meetupName = meetupName;
    }

    public LocalDate getMeetupDate() {
        return meetupDate;
    }

    public void setMeetupDate(LocalDate meetupDate) {
        this.meetupDate = meetupDate;
    }

    public LocalTime getMeetupTime() {
        return meetupTime;
    }

    public void setMeetupTime(LocalTime meetupTime) {
        this.meetupTime = meetupTime;
    }
}
