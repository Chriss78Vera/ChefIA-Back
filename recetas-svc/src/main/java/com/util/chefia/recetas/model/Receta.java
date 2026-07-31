package com.util.chefia.recetas.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "recetas")
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor
public class Receta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String nombre;
    @Column(nullable = false, length = 1000)
    private String descripcion;
    @Column(nullable = false)
    private Integer tiempoMinutos;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Dificultad dificultad;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAlimentacion tipoAlimentacion;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ingredientes_receta", joinColumns = @JoinColumn(name = "receta_id"))
    @Column(name = "ingrediente")
    private Set<String> ingredientes = new LinkedHashSet<>();
    private Integer porciones = 2;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pasos_receta", joinColumns = @JoinColumn(name = "receta_id"))
    @Column(name = "paso", length = 1000)
    private List<String> pasos = new ArrayList<>();
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tags_receta", joinColumns = @JoinColumn(name = "receta_id"))
    @Column(name = "tag")
    private Set<String> tags = new LinkedHashSet<>();
    private Boolean publica = true;
    @Column(name = "usuario_sub", length = 100)
    private String usuarioSub;
    @Enumerated(EnumType.STRING)
    private Animo animo;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_receta")
    private TipoReceta tipoReceta;

    @Column(name = "ingrediente_aditional", length = 100)
    private String ingredienteAditional;

    public boolean isPublica() {
        return publica == null || publica;
    }

    public void actualizar(String n, String d, Integer t, Dificultad dif, TipoAlimentacion tipo, Set<String> ing,
            String ingAditional) {
        nombre = n;
        descripcion = d;
        tiempoMinutos = t;
        dificultad = dif;
        tipoAlimentacion = tipo;
        ingredienteAditional = normalizarIngredienteAditional(ingAditional);
        ingredientes = new LinkedHashSet<>(ing);
    }

    public void configurarUsuario(String sub, boolean esPublica, Animo estado, TipoReceta tipo, Integer cantidad,
            List<String> preparacion, Set<String> etiquetas, String ingAditional) {
        usuarioSub = sub;
        publica = esPublica;
        animo = estado;
        tipoReceta = tipo;
        porciones = cantidad;
        pasos = new ArrayList<>(preparacion);
        tags = new LinkedHashSet<>(etiquetas);
        ingredienteAditional = normalizarIngredienteAditional(ingAditional);
    }

    public void definirTipoReceta(TipoReceta tipo) {
        tipoReceta = tipo;
    }

    private String normalizarIngredienteAditional(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
