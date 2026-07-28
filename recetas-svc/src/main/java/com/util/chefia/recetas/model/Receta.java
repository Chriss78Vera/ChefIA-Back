package com.util.chefia.recetas.model;
import jakarta.persistence.*;
import java.util.*;
@Entity
@Table(name="recetas")
public class Receta {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String nombre;
 @Column(nullable=false,length=1000) private String descripcion;
 @Column(nullable=false) private Integer tiempoMinutos;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private Dificultad dificultad;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private TipoAlimentacion tipoAlimentacion;
 @ElementCollection(fetch=FetchType.EAGER)
 @CollectionTable(name="ingredientes_receta",joinColumns=@JoinColumn(name="receta_id"))
 @Column(name="ingrediente") private Set<String> ingredientes=new LinkedHashSet<>();
 private Integer porciones=2;
 @ElementCollection(fetch=FetchType.EAGER) @CollectionTable(name="pasos_receta",joinColumns=@JoinColumn(name="receta_id"))
 @Column(name="paso",length=1000) private List<String> pasos=new ArrayList<>();
 @ElementCollection(fetch=FetchType.EAGER) @CollectionTable(name="tags_receta",joinColumns=@JoinColumn(name="receta_id"))
 @Column(name="tag") private Set<String> tags=new LinkedHashSet<>();
 private Boolean publica=true;
 @Column(name="usuario_sub",length=100) private String usuarioSub;
 @Enumerated(EnumType.STRING) private Animo animo;
 @Enumerated(EnumType.STRING) @Column(name="tipo_receta") private TipoReceta tipoReceta;
 public Receta(){}
 public Long getId(){return id;} public String getNombre(){return nombre;} public String getDescripcion(){return descripcion;}
 public Integer getTiempoMinutos(){return tiempoMinutos;} public Dificultad getDificultad(){return dificultad;}
 public TipoAlimentacion getTipoAlimentacion(){return tipoAlimentacion;} public Set<String> getIngredientes(){return ingredientes;}
 public Integer getPorciones(){return porciones;} public List<String> getPasos(){return pasos;} public Set<String> getTags(){return tags;}
 public boolean isPublica(){return publica==null||publica;} public String getUsuarioSub(){return usuarioSub;} public Animo getAnimo(){return animo;}
 public TipoReceta getTipoReceta(){return tipoReceta;}
 public void actualizar(String n,String d,Integer t,Dificultad dif,TipoAlimentacion tipo,Set<String> ing){
  nombre=n;descripcion=d;tiempoMinutos=t;dificultad=dif;tipoAlimentacion=tipo;ingredientes=new LinkedHashSet<>(ing);
 }
 public void configurarUsuario(String sub,boolean esPublica,Animo estado,TipoReceta tipo,Integer cantidad,List<String> preparacion,Set<String> etiquetas){
  usuarioSub=sub;publica=esPublica;animo=estado;tipoReceta=tipo;porciones=cantidad;pasos=new ArrayList<>(preparacion);tags=new LinkedHashSet<>(etiquetas);
 }
 public void definirTipoReceta(TipoReceta tipo){tipoReceta=tipo;}
}

