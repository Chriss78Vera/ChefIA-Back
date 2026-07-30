package com.util.chefia.recomendaciones.config;

import java.util.*;
import org.springframework.context.annotation.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {
    @Bean
    SecurityFilterChain security(HttpSecurity http) throws Exception {
        return http.csrf(c -> c.disable()).authorizeHttpRequests(a -> a
                .requestMatchers("/actuator/health/**").permitAll().anyRequest().authenticated())
                .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(converter()))).build();
    }

    @Bean
    WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    private Converter<Jwt, AbstractAuthenticationToken> converter() {
        return jwt -> {
            Map<String, Object> m = jwt.getClaimAsMap("realm_access");
            Collection<?> rs = m == null ? List.of() : (Collection<?>) m.getOrDefault("roles", List.of());
            return new JwtAuthenticationToken(jwt,
                    rs.stream().map(String::valueOf).map(r -> new SimpleGrantedAuthority("ROLE_" + r)).toList(),
                    jwt.getClaimAsString("preferred_username"));
        };
    }
}
