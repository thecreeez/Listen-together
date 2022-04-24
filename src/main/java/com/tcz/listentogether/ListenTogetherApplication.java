package com.tcz.listentogether;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;
import java.util.Date;

@SpringBootApplication
public class ListenTogetherApplication {

	@Bean
	public MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setMaxFileSize(DataSize.parse("10MB"));
		factory.setMaxRequestSize(DataSize.parse("10MB"));
		return factory.createMultipartConfig();
	}

	public static void main(String[] args) {
		SpringApplication.run(ListenTogetherApplication.class, args);
	}
}