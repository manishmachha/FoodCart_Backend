package com.app.foodcart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FoodcartApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodcartApplication.class, args);
	}

}
	