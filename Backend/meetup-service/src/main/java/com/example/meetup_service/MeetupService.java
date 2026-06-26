package com.example.meetup_service;

import com.example.user_service.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class MeetupService {
    private final MeetupRepository meetupRepository;

    public MeetupService(MeetupRepository meetupRepository) {
        this.meetupRepository = meetupRepository;
    }

    public ResponseEntity<MeetupResponseDTO> createMeetup(MeetupRequestDTO meetupRequestDTO) {
        MeetupEntity newEntity = toEntity(meetupRequestDTO);
        MeetupEntity saved = meetupRepository.save(newEntity);

        //use response entity for  return message
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(saved));
    }

    public MeetupEntity toEntity(MeetupRequestDTO meetupRequestDTO) {
        UserEntity creator = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new MeetupEntity(creator.getId(), meetupRequestDTO.getLat(),
                meetupRequestDTO.getLng(),
                meetupRequestDTO.getMeetupName(),
                meetupRequestDTO.getMeetupDate(),
                meetupRequestDTO.getMeetupTime());
    }

    public MeetupResponseDTO toResponseDTO(MeetupEntity meetupEntity) {
        return new MeetupResponseDTO(meetupEntity.getLat(),
                meetupEntity.getLng(),
                meetupEntity.getMeetupName(),
                meetupEntity.getMeetupDate(),
                meetupEntity.getMeetupTime());
    }

    public List<MeetupResponseDTO> getMeetups() {
        return meetupRepository.findAll().stream()
                .map(this::toResponseDTO).collect(Collectors.toList());
    }

    public List<MeetupResponseDTO> getActiveMeetups() {
        //get all meetups marked as active meetups (might be stale or not)
        List<MeetupEntity> currentActiveMeetups = meetupRepository.findByMeetupStatus(MeetupStatus.ACTIVE);

        //filter expired meetups from the stale active meetups
        List<MeetupEntity> expiredMeetups = currentActiveMeetups.stream()
                .filter(meetupEntity -> meetupEntity.getMeetupStatus() == MeetupStatus.ACTIVE && LocalDateTime.now().isAfter(meetupEntity.getMeetupDate().atTime(meetupEntity.getMeetupTime())))
                .collect(Collectors.toList());

        //mark status of expired meetups as EXPIRED, and update repository
        expiredMeetups.forEach(meetupEntity -> meetupEntity.setMeetupStatus(MeetupStatus.EXPIRED));
        meetupRepository.saveAll(expiredMeetups);

        //return the updated active ones now
        return meetupRepository.findByMeetupStatus(MeetupStatus.ACTIVE).stream()
                .map(this::toResponseDTO).collect(Collectors.toList());
    }

}
