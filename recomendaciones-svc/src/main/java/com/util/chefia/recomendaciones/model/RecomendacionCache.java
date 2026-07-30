package com.util.chefia.recomendaciones.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import com.util.chefia.recomendaciones.dto.RecomendacionDto.TipoReceta;

@Entity
@Table(name = "recomendaciones_cache")
public class RecomendacionCache {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "usuario_sub", length = 100)
    private String usuarioSub;
    @Column(nullable = false, length = 30)
    private String animo;
    @Column(name = "tipo_receta", length = 30)
    private String tipoReceta;
    @Column(nullable = false, length = 100)
    private String preferencia;
    @Column(name = "recomendaciones_json", nullable = false, columnDefinition = "TEXT")
    private String recomendacionesJson;
    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn;

    protected RecomendacionCache() {
    }

    public RecomendacionCache(String usuarioSub, String animo, TipoReceta tipoReceta, String preferencia,
            String recomendacionesJson, Instant creadoEn) {
        this.usuarioSub = usuarioSub;
        this.animo = animo;
        this.tipoReceta = tipoReceta.name();
        this.preferencia = preferencia;
        this.recomendacionesJson = recomendacionesJson;
        this.creadoEn = creadoEn;
    }

    public Long getId() {
        return id;
    }

    public String getUsuarioSub() {
        return usuarioSub;
    }

    public String getAnimo() {
        return animo;
    }

    public String getTipoReceta() {
        return tipoReceta;
    }

    public String getPreferencia() {
        return preferencia;
    }

    public String getRecomendacionesJson() {
        return recomendacionesJson;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }
}
