package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.domain.EntityScan;


//@SpringBootApplication(scanBasePackages = {
////		"com.example.meetup_service",
////		"com.example.user_service"
////})
////// 1. Tells Spring where to find your UserRepository
////@EnableJpaRepositories(basePackages = {
////		"com.example.meetup_service",
////		"com.example.user_service"
////})
////// 2. Tells Spring where to find your UserEntity and MeetupEntity
////@EntityScan(basePackages = {
////		"com.example.meetup_service",
////		"com.example.user_service"
////})

//@Import(SecurityConfig.class)

//put SpringBootApp in the parent folder to avoid scanning diff packages along with its errors
//it will automatically scan all child packages for components
@SpringBootApplication
public class RendevooApplication {

	public static void main(String[] args) {
		SpringApplication.run(RendevooApplication.class, args);
	}
}