package com.rllc.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableAutoConfiguration
public class WebcastApplication {

	public static void main(String[] args) {
		SpringApplication.exit(SpringApplication.run(WebcastApplication.class,
				args));
	}

}
