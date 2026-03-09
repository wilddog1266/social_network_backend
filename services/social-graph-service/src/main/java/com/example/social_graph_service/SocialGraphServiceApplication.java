package com.example.social_graph_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SocialGraphServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialGraphServiceApplication.class, args);
	}

}
