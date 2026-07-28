package com.util.chefia.usuarios.mapper;

import com.util.chefia.usuarios.dto.PerfilResponse;
import com.util.chefia.usuarios.model.PerfilUsuario;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class PerfilMapper {
    public PerfilResponse aResponse(PerfilUsuario perfil) {
        return new PerfilResponse(
            perfil.getKeycloakSub(),
            perfil.getNombre(),
            perfil.getEmail(),
            perfil.getTipoAlimentacion(),
            Set.copyOf(perfil.getRestricciones()),
            Set.copyOf(perfil.getIngredientesNoDeseados()),
            perfil.getActualizadoEn()
        );
    }
}

