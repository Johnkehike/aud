package com.Auditionapp.Audition;

import com.google.gson.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCrypt;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import java.io.IOException;

@SpringBootApplication
//@EnableEurekaClient
public class AuditionApplication {


	public static void main(String[] args) throws IOException {
		SpringApplication.run(AuditionApplication.class, args);

		String hashed_password = BCrypt.hashpw("test", BCrypt.gensalt());
		System.out.println(hashed_password);



	}

}
