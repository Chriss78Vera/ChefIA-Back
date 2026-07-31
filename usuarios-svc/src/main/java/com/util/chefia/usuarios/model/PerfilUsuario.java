package com.util.chefia.usuarios.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "perfiles_usuario")
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerfilUsuario {
    @Id
    private String keycloakSub;
    private String nombre;
    private String email;
    @Enumerated(EnumType.STRING)
    private TipoAlimentacion tipoAlimentacion = TipoAlimentacion.OMNIVORO;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "restricciones_usuario", joinColumns = @JoinColumn(name = "usuario_sub"))
    @Column(name = "restriccion")
    private Set<String> restricciones = new HashSet<>();
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ingredientes_no_deseados", joinColumns = @JoinColumn(name = "usuario_sub"))
    @Column(name = "ingrediente")
    private Set<String> ingredientesNoDeseados = new HashSet<>();
    private Instant actualizadoEn = Instant.now();

    public PerfilUsuario(String sub, String nombre, String email) {
        this.keycloakSub = sub;
        this.nombre = nombre;
        this.email = email;
    }

    public void actualizar(TipoAlimentacion tipo, Set<String> restricciones, Set<String> noDeseados) {
        this.tipoAlimentacion = tipo;
        this.restricciones = new HashSet<>(restricciones);
        this.ingredientesNoDeseados = new HashSet<>(noDeseados);
        this.actualizadoEn = Instant.now();
    }
}
