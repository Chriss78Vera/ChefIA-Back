package com.util.chefia.auth.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.util.chefia.auth.dto.CrearUsuarioRequest;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class KeycloakMapperTest {
    @Test
    void registroSiempreSolicitaRolUsuario() {
        var request = new CrearUsuarioRequest(
            "nuevo.usuario", "nuevo@chefia.local", "Nuevo", "Usuario", "ClaveSegura123!");

        Map<String, Object> representation = new KeycloakMapper().aUsuario(request, false);

        assertThat(representation.get("realmRoles")).isEqualTo(List.of("USUARIO"));
        assertThat(String.valueOf(representation)).doesNotContain("ADMIN");
    }

    @Test
    void creacionAdministrativaUsaContraseniaTemporal() {
        var request = new CrearUsuarioRequest(
            "temporal", "temporal@chefia.local", "Usuario", "Temporal", "ClaveTemporal123!");

        Map<String, Object> representation = new KeycloakMapper().aUsuario(request, true);
        var credenciales = (List<Map<String, Object>>) representation.get("credentials");

        assertThat(representation.get("requiredActions")).isEqualTo(List.of("UPDATE_PASSWORD"));
        assertThat(credenciales.get(0).get("temporary")).isEqualTo(true);
    }
}
