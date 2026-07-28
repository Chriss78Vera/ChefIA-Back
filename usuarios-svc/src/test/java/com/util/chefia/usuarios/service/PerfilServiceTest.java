package com.util.chefia.usuarios.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import com.util.chefia.usuarios.dto.PreferenciasRequest;
import com.util.chefia.usuarios.model.*;
import com.util.chefia.usuarios.repository.PerfilRepository;
import com.util.chefia.usuarios.service.impl.PerfilServiceImpl;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

class PerfilServiceTest {
    @Test
    void creaElPerfilConElSubDelJwtYActualizaPreferencias() {
        PerfilRepository repository = mock(PerfilRepository.class);
        Jwt jwt = Jwt.withTokenValue("token").header("alg", "none")
            .subject("usuario-123").claim("name", "Ana").claim("email", "ana@chefia.local").build();
        when(repository.findById("usuario-123")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        PerfilService service = new PerfilServiceImpl(repository);

        PerfilUsuario result = service.actualizar(jwt,
            new PreferenciasRequest(TipoAlimentacion.VEGANO, Set.of("SIN_LACTOSA"), Set.of("mani")));

        assertThat(result.getKeycloakSub()).isEqualTo("usuario-123");
        assertThat(result.getTipoAlimentacion()).isEqualTo(TipoAlimentacion.VEGANO);
        verify(repository, atLeastOnce()).save(any(PerfilUsuario.class));
    }
}


