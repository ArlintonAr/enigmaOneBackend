package com.enigmaOne.enigmaOne;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories // especifica que se usará los repositorios de spring
@EnableJpaAuditing //Auditoria de la aplicacion
public class EnigmaOneApplication {
	public static void main(String[] args) {
		SpringApplication.run(EnigmaOneApplication.class, args);
	}

}
