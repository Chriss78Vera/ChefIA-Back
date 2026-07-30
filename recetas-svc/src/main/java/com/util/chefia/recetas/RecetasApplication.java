package com.util.chefia.recetas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableMethodSecurity
@SpringBootApplication
public class RecetasApplication {
    public static void main(String[] args) {
        SpringApplication.run(RecetasApplication.class, args);
    }
}
