package com.util.chefia.recomendaciones.controller;

import com.util.chefia.recomendaciones.dto.RecomendacionDto.*;
import com.util.chefia.recomendaciones.service.RecomendacionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class RecomendacionController {
    private final RecomendacionService service;

    public RecomendacionController(RecomendacionService s) {
        service = s;
    }

    @PostMapping({ "/api/recomendar", "/api/recomendar/" })
    @PreAuthorize("hasAnyRole('USUARIO','ADMIN')")
    public Response recomendar(@Valid @RequestBody Request request, @AuthenticationPrincipal Jwt jwt) {
        return service.recomendar(request.animo(), request.tipoReceta(), jwt.getSubject(), jwt.getTokenValue());
    }

    @GetMapping({ "/api/recomendaciones", "/api/recomendaciones/" })
    @PreAuthorize("hasAnyRole('USUARIO','ADMIN')")
    public List<HistorialResponse> historial(@AuthenticationPrincipal Jwt jwt) {
        return service.historial(jwt.getSubject());
    }
}
