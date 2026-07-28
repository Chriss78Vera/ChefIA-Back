package com.util.chefia.auth.mapper;

import com.util.chefia.auth.dto.CrearUsuarioRequest;
import com.util.chefia.auth.dto.TokenResponse;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class KeycloakMapper {
    public Map<String, Object> aUsuario(CrearUsuarioRequest request, boolean temporal) {
        return Map.of(
            "username", request.username(),
            "email", request.email(),
            "firstName", request.nombre(),
            "lastName", request.apellido(),
            "enabled", true,
            "emailVerified", true,
            "requiredActions", temporal ? List.of("UPDATE_PASSWORD") : List.of(),
            "realmRoles", List.of("USUARIO"),
            "credentials", List.of(Map.of(
                "type", "password",
                "value", request.password(),
                "temporary", temporal)));
    }

    public TokenResponse aToken(Map<String, Object> response) {
        return new TokenResponse(
            String.valueOf(response.get("access_token")),
            String.valueOf(response.get("refresh_token")),
            numero(response.get("expires_in")),
            numero(response.get("refresh_expires_in")),
            String.valueOf(response.get("token_type")));
    }

    private long numero(Object value) {
        return value instanceof Number number ? number.longValue() : 0L;
    }
}

