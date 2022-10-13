package com.project.edithandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class EditHandlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EditHandlerApplication.class, args);
	}

}
