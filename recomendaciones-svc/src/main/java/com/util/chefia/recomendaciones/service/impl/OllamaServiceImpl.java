package com.util.chefia.recomendaciones.service.impl;

import com.util.chefia.recomendaciones.client.*;
import com.util.chefia.recomendaciones.dto.RecomendacionDto.*;
import com.util.chefia.recomendaciones.service.OllamaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.text.Normalizer;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OllamaServiceImpl implements OllamaService {
   private final WebClient client;
   private final ObjectMapper mapper;
   private final String model;

   public OllamaServiceImpl(WebClient.Builder builder, ObjectMapper mapper,
         @Value("${ollama.base-url}") String baseUrl, @Value("${ollama.model}") String model) {
      this.client = builder.baseUrl(baseUrl).build();
      this.mapper = mapper;
      this.model = model;
   }

   @CircuitBreaker(name = "ollama")
   @Retry(name = "ollama")
   @Override
   public List<Item> recomendar(Animo animo, TipoReceta tipoReceta, UsuariosClient.Preferencias pref,
         List<RecetasClient.RecetaCandidata> recetas, int cantidad) {
      String prompt = """
            Eres ChefIA. Crea exactamente %s recetas adecuadas al animo y preferencias.
            Devuelve exactamente %s recomendaciones y cumple estrictamente el esquema JSON proporcionado.
            Genera una receta completa y realizable para cada recomendacion.
            Crea un nombre unico y descriptivo para cada plato. El nombre debe corresponder con
            sus ingredientes y sus pasos. No copies el mismo nombre en varias recomendaciones.
            Usa las candidatas como inspiracion, pero puedes crear platos y nombres diferentes.
            Incluye una descripcion de maximo 15 palabras, numero de porciones y maximo 7 ingredientes con cantidades y unidades
            como textos simples, y pasos de preparacion claros en el orden correcto.
            El campo ingredientes debe ser una lista de textos simples, nunca una lista de objetos.
            El campo pasos debe contener exactamente 3 instrucciones concretas, una accion por elemento.
            Incluye exactamente 2 tags cortos en minusculas para cada receta, por ejemplo:
            vegano, reconfortante, saludable, rapido, energetico o fresco.
            Usa solamente caracteres ASCII. No uses tildes. Sustituye la letra enie por ni.
            Todas las recetas deben pertenecer al tipo solicitado: %s.
            Definicion obligatoria del tipo: DESAYUNO es comida matutina; ENTRANTE es una porcion ligera inicial;
            SOPA es caldo, crema o sopa; PLATO_FUERTE es una comida principal; POSTRE es una preparacion dulce.
            Ejemplos validos: DESAYUNO avena o huevos; ENTRANTE ensalada o bruschetta; SOPA caldo o crema;
            PLATO_FUERTE arroz, pasta o proteina; POSTRE flan, pastel, mousse, helado o fruta dulce.
            Animo: %s. Alimentacion: %s. Restricciones: %s. Ingredientes no deseados: %s.
            Candidatas: %s
            """
            .formatted(cantidad, cantidad, tipoReceta, animo, pref.tipoAlimentacion(), pref.restricciones(),
                  pref.ingredientesNoDeseados(), recetas);
      Map<?, ?> response = client.post().uri("/api/generate").bodyValue(Map.of(
            "model", model,
            "prompt", prompt,
            "stream", false,
            "format", schema(cantidad, tipoReceta),
            "keep_alive", "10m",
            "options", Map.of("temperature", 0.5))).retrieve().bodyToMono(Map.class).block();
      if (response == null || response.get("response") == null)
         throw new IllegalStateException("Ollama no devolvio contenido");
      try {
         AiPayload parsed = mapper.readValue(aAscii(String.valueOf(response.get("response"))), AiPayload.class);
         if (parsed.recomendaciones() == null || parsed.recomendaciones().size() != cantidad)
            throw new IllegalStateException("Ollama devolvio una cantidad incorrecta de recetas");
         long nombresUnicos = parsed.recomendaciones().stream()
               .map(Item::nombre).map(nombre -> nombre.toLowerCase(Locale.ROOT).trim()).distinct().count();
         if (nombresUnicos != cantidad)
            throw new IllegalStateException("Ollama debe devolver nombres diferentes");
         boolean tiposValidos = parsed.recomendaciones().stream()
               .allMatch(i -> i.tipoReceta() == tipoReceta && esCompatible(i, tipoReceta));
         if (!tiposValidos) {
            log.warn("Ollama devolvio recetas incompatibles con {}: {}", tipoReceta,
                  parsed.recomendaciones().stream().map(Item::nombre).toList());
            throw new IllegalStateException("Ollama devolvio una receta incompatible con el tipo solicitado");
         }
         return parsed.recomendaciones().stream()
               .map(i -> new Item(i.nombre(), i.motivo(), i.descripcion(), i.porciones(),
                     i.tiempoMinutos(), i.dificultad(), i.ingredientes(), i.pasos(), i.tags(), null, "OLLAMA", false,
                     tipoReceta))
               .toList();
      } catch (Exception ex) {
         throw new IllegalStateException("Respuesta JSON de Ollama invalida", ex);
      }
   }

   private String aAscii(String value) {
      String conEnie = value.replace("\u00f1", "ni").replace("\u00d1", "Ni");
      String normalizado = Normalizer.normalize(conEnie, Normalizer.Form.NFD);
      return normalizado.replaceAll("\\p{M}", "").replaceAll("[^\\x00-\\x7F]", "");
   }

   private Map<String, Object> schema(int cantidad, TipoReceta tipoReceta) {
      Map<String, Object> item = Map.of(
            "type", "object",
            "additionalProperties", false,
            "required",
            List.of("nombre", "motivo", "descripcion", "porciones", "tiempoMinutos", "dificultad", "ingredientes",
                  "pasos", "tags", "tipoReceta"),
            "properties", Map.of(
                  "nombre", Map.of("type", "string", "minLength", 3),
                  "motivo", Map.of("type", "string"),
                  "descripcion", Map.of("type", "string", "maxLength", 120),
                  "porciones", Map.of("type", "integer", "minimum", 1, "maximum", 12),
                  "tiempoMinutos", Map.of("type", "integer", "minimum", 1),
                  "dificultad", Map.of("type", "string", "enum", List.of("FACIL", "MEDIA", "DIFICIL")),
                  "ingredientes",
                  Map.of("type", "array", "minItems", 1, "maxItems", 7, "items", Map.of("type", "string")),
                  "pasos", Map.of("type", "array", "minItems", 3, "maxItems", 3, "items", Map.of("type", "string")),
                  "tags", Map.of("type", "array", "minItems", 2, "maxItems", 2, "items", Map.of("type", "string")),
                  "tipoReceta", Map.of("type", "string", "enum", List.of(tipoReceta.name()))));
      return Map.of(
            "type", "object",
            "additionalProperties", false,
            "required", List.of("recomendaciones"),
            "properties", Map.of("recomendaciones", Map.of(
                  "type", "array", "minItems", cantidad, "maxItems", cantidad, "items", item)));
   }

   private boolean esCompatible(Item item, TipoReceta tipo) {
      String texto = (item.nombre() + " " + item.descripcion() + " " + String.join(" ", item.tags()))
            .toLowerCase(Locale.ROOT);
      List<String> claves = switch (tipo) {
         case DESAYUNO ->
            List.of("desayuno", "avena", "huevo", "tostada", "pancake", "cereal", "yogur", "arepa", "waffle");
         case ENTRANTE -> List.of("entrante", "ensalada", "bruschetta", "bocado", "aperitivo", "tapa", "rollito",
               "ceviche", "carpaccio");
         case SOPA -> List.of("sopa", "caldo", "crema", "consome");
         case PLATO_FUERTE ->
            List.of("arroz", "pollo", "carne", "pasta", "pescado", "lasania", "guiso", "curry", "plato fuerte");
         case POSTRE -> List.of("postre", "flan", "pastel", "tarta", "mousse", "helado", "brownie", "galleta", "pudin",
               "dulce", "fruta", "tiramisu", "chocolate", "cheesecake", "bizcocho", "cupcake", "arroz con leche",
               "crema catalana");
      };
      return claves.stream().anyMatch(texto::contains);
   }
}
