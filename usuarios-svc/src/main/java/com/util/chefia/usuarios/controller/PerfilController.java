package com.util.chefia.usuarios.controller;

import com.util.chefia.usuarios.dto.PreferenciasRequest;
import com.util.chefia.usuarios.dto.PerfilResponse;
import com.util.chefia.usuarios.mapper.PerfilMapper;
import com.util.chefia.usuarios.service.PerfilService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasAnyRole('USUARIO','ADMIN')")
public class PerfilController {
    private final PerfilService service;
    private final PerfilMapper mapper;
    public PerfilController(PerfilService service, PerfilMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping({"/perfil", "/preferencias"})
    public PerfilResponse obtener(@AuthenticationPrincipal Jwt jwt) { return mapper.aResponse(service.obtenerOCrear(jwt)); }

    @PutMapping("/preferencias")
    public PerfilResponse actualizar(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody PreferenciasRequest request) {
        return mapper.aResponse(service.actualizar(jwt, request));
    }
}

