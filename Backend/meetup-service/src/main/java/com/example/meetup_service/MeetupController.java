package com.example.meetup_service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5500")
public class MeetupController {
    private final MeetupService meetupService;

    public MeetupController(MeetupService meetupService) {
        this.meetupService = meetupService;
    }

    @PostMapping("backend/meetup")
    public ResponseEntity<MeetupResponseDTO> createMeetup (@RequestBody MeetupRequestDTO meetupRequestDTO){
        return meetupService.createMeetup(meetupRequestDTO);
    }

    @GetMapping("backend/meetup")
    public List<MeetupResponseDTO> getMeetups () {
        return meetupService.getActiveMeetups();
    }
}
