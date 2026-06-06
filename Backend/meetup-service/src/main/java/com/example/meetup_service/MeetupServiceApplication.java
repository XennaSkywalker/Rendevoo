package com.example.meetup_service;

import com.example.user_service.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
		"com.example.meetup_service",
		"com.example.user_service"
})
// 1. Tells Spring where to find your UserRepository
@EnableJpaRepositories(basePackages = {
		"com.example.meetup_service",
		"com.example.user_service"
})
// 2. Tells Spring where to find your UserEntity and MeetupEntity
@EntityScan(basePackages = {
		"com.example.meetup_service",
		"com.example.user_service"
})
@Import(SecurityConfig.class)
public class MeetupServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MeetupServiceApplication.class, args);
	}
}