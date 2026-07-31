package com.util.chefia.favoritos.model;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "favoritos", uniqueConstraints = @UniqueConstraint(columnNames = { "usuarioSub", "recetaId" }))
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public Favorito(String sub, Long recetaId, String nombre) {
        this.usuarioSub = sub;
        this.recetaId = recetaId;
        this.recetaNombre = nombre;
    }

}
