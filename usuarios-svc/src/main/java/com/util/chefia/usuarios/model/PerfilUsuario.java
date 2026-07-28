package com.util.chefia.usuarios.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "perfiles_usuario")
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

    protected PerfilUsuario() {}
    public PerfilUsuario(String sub, String nombre, String email) {
        this.keycloakSub = sub; this.nombre = nombre; this.email = email;
    }
    public String getKeycloakSub() { return keycloakSub; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public TipoAlimentacion getTipoAlimentacion() { return tipoAlimentacion; }
    public Set<String> getRestricciones() { return restricciones; }
    public Set<String> getIngredientesNoDeseados() { return ingredientesNoDeseados; }
    public Instant getActualizadoEn() { return actualizadoEn; }
    public void actualizar(TipoAlimentacion tipo, Set<String> restricciones, Set<String> noDeseados) {
        this.tipoAlimentacion = tipo;
        this.restricciones = new HashSet<>(restricciones);
        this.ingredientesNoDeseados = new HashSet<>(noDeseados);
        this.actualizadoEn = Instant.now();
    }
}


