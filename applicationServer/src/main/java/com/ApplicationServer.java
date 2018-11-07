package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class ApplicationServer {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationServer.class, args);
	}
}
