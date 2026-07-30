package com.util.chefia.favoritos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableMethodSecurity
@SpringBootApplication
public class FavoritosApplication {
    public static void main(String[] args) {
        SpringApplication.run(FavoritosApplication.class, args);
    }
}
