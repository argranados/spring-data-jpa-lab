package com.ciberaccion.ecommercelab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ECommerceLabApplication {

	public static void main(String[] args) {
		SpringApplication.run(ECommerceLabApplication.class, args);
	}

}
