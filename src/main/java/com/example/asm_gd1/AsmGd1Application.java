package com.example.asm_gd1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.example.asm_gd1")
@EnableJpaRepositories(basePackages = "com.example.asm_gd1.repository")
@EntityScan(basePackages = "com.example.asm_gd1.model")
public class AsmGd1Application {

	public static void main(String[] args) {
		SpringApplication.run(AsmGd1Application.class, args);
	}

}
