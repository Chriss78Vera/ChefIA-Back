package com.util.chefia.favoritos.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "favoritos", uniqueConstraints = @UniqueConstraint(columnNames = { "usuarioSub", "recetaId" }))
public class Favorito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String usuarioSub;
    @Column(nullable = false)
    private Long recetaId;
    @Column(nullable = false)
    private String recetaNombre;
    private Instant creadoEn = Instant.now();

    protected Favorito() {
    }

    public Favorito(String sub, Long recetaId, String nombre) {
        this.usuarioSub = sub;
        this.recetaId = recetaId;
        this.recetaNombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public String getUsuarioSub() {
        return usuarioSub;
    }

    public Long getRecetaId() {
        return recetaId;
    }

    public String getRecetaNombre() {
        return recetaNombre;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }
}
