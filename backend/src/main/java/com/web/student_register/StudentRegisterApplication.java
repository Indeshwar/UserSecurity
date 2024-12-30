package com.web.student_register;

import com.web.student_register.config.RateLimiterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class StudentRegisterApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudentRegisterApplication.class, args);
	}

}
