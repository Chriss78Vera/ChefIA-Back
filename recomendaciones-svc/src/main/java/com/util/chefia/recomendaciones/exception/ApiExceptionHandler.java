package com.util.chefia.recomendaciones.exception;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
@RestControllerAdvice
public class ApiExceptionHandler {
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<Map<String,Object>> invalid(){return error(400,"El animo es obligatorio y debe ser valido");}
 @ExceptionHandler(WebClientResponseException.class) ResponseEntity<Map<String,Object>> dependency(){return error(503,"No fue posible consultar las preferencias del usuario");}
 @ExceptionHandler(IllegalStateException.class) ResponseEntity<Map<String,Object>> unavailable(IllegalStateException e){return error(503,e.getMessage());}
 private ResponseEntity<Map<String,Object>> error(int s,String m){return ResponseEntity.status(s).body(Map.of("timestamp",Instant.now(),"status",s,"message",m));}
}

