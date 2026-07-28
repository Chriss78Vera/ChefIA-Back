package com.util.chefia.usuarios.service;

import com.util.chefia.usuarios.dto.PreferenciasRequest;
import com.util.chefia.usuarios.model.PerfilUsuario;
import org.springframework.security.oauth2.jwt.Jwt;

public interface PerfilService {
    PerfilUsuario obtenerOCrear(Jwt jwt);
    PerfilUsuario actualizar(Jwt jwt, PreferenciasRequest request);
}


