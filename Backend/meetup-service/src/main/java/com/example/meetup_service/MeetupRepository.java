package com.example.meetup_service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MeetupRepository extends JpaRepository<MeetupEntity, Long> {
    List<MeetupEntity> findByMeetupStatus(MeetupStatus meetupStatus);

    @Query (value = "SELECT * FROM (\n" +
            "  SELECT *, 6371 * ACOS(\n" +
            "    COS(RADIANS(:lat)) * COS(RADIANS(lat)) *\n" +
            "    COS(RADIANS(lng) - RADIANS(:lng)) +\n" +
            "    SIN(RADIANS(:lat)) * SIN(RADIANS(lat))\n" +
            "  ) AS distance \n" +
            "  FROM meetups \n" +
            "  WHERE lat BETWEEN :lat - (:radius / 111.0) AND :lat + (:radius / 111.0)\n" +
            "  AND lng BETWEEN :lng - (:radius / 111.0) AND :lng + (:radius / 111.0)\n" +
            "  AND meetup_status = 'ACTIVE'\n" +
            ") AS nearby\n" +
            "WHERE nearby.distance < :radius",
    nativeQuery = true)
    List<MeetupEntity> findNearbyMeetups(@Param("lat") double lat, @Param("lng") double lng, @Param("radius") int radius);

    List<MeetupEntity> findByCreatorId(long creatorId);


    //things to do:
    //deleting a user and,
    //setting creatorId to null when a user is deleted

}
