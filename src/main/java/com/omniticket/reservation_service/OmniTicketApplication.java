package com.omniticket.reservation_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OmniTicketApplication {

	public static void main(String[] args) {
		SpringApplication.run(OmniTicketApplication.class, args);
	}

}
