package com.util.chefia.auth.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableReactiveMethodSecurity
/** Configura el servicio como Resource Server y traduce los roles del realm a autoridades de Spring. */
public class SecurityConfig {
    /** Declara las rutas públicas; cualquier otra operación exige un JWT válido. */
    @Bean
    SecurityWebFilterChain security(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        .pathMatchers("/api/auth/login", "/api/auth/registro",
                                "/api/auth/contrasenia-temporal", "/actuator/health/**")
                        .permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(converter())))
                .build();
    }

    /** Convierte realm_access.roles de Keycloak a autoridades con el prefijo ROLE_. */
    private Converter<Jwt, ? extends reactor.core.publisher.Mono<? extends AbstractAuthenticationToken>> converter() {
        return jwt -> {
            Map<String, Object> access = jwt.getClaimAsMap("realm_access");
            Collection<?> roles = access == null ? List.of() : (Collection<?>) access.getOrDefault("roles", List.of());
            var authorities = roles.stream()
                    .map(String::valueOf)
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .toList();
            return reactor.core.publisher.Mono.just(
                    new JwtAuthenticationToken(jwt, authorities, jwt.getClaimAsString("preferred_username")));
        };
    }
}
