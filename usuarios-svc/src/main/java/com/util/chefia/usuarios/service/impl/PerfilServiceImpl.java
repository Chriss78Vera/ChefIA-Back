package com.util.chefia.usuarios.service.impl;

import com.util.chefia.usuarios.dto.PreferenciasRequest;
import com.util.chefia.usuarios.model.PerfilUsuario;
import com.util.chefia.usuarios.repository.PerfilRepository;
import com.util.chefia.usuarios.service.PerfilService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PerfilServiceImpl implements PerfilService {
    private final PerfilRepository repository;

    public PerfilServiceImpl(PerfilRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public PerfilUsuario obtenerOCrear(Jwt jwt) {
        return repository.findById(jwt.getSubject()).orElseGet(() -> repository.save(
            new PerfilUsuario(jwt.getSubject(), value(jwt, "name"), value(jwt, "email"))));
    }

    @Override
    @Transactional
    public PerfilUsuario actualizar(Jwt jwt, PreferenciasRequest request) {
        PerfilUsuario perfil = obtenerOCrear(jwt);
        perfil.actualizar(request.tipoAlimentacion(), request.restricciones(), request.ingredientesNoDeseados());
        return repository.save(perfil);
    }

    private String value(Jwt jwt, String claim) {
        String value = jwt.getClaimAsString(claim);
        return value == null ? "" : value;
    }
}
