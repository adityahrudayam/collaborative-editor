package com.project.instruction.publisher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class InstructionPublisherApplication {

	public static void main(String[] args) {
		SpringApplication.run(InstructionPublisherApplication.class, args);
	}

}
