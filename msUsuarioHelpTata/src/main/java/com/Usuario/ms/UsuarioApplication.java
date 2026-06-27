package com.Usuario.ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//* Clase principal del microservicio de usuarios
//? @SpringBootApplication equivale a @Configuration + @EnableAutoConfiguration + @ComponentScan
@SpringBootApplication
public class UsuarioApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsuarioApplication.class, args);
	}

}
