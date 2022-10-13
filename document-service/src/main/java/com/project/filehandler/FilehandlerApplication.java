package com.project.filehandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class FilehandlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FilehandlerApplication.class, args);
	}

}
