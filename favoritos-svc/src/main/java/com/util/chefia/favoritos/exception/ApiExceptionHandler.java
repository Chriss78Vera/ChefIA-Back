package com.util.chefia.favoritos.exception;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestControllerAdvice
public class ApiExceptionHandler {
 @ExceptionHandler(ResponseStatusException.class) ResponseEntity<Map<String,Object>> status(ResponseStatusException e){return error(e.getStatusCode().value(),e.getReason());}
 @ExceptionHandler(WebClientResponseException.NotFound.class) ResponseEntity<Map<String,Object>> missing(){return error(404,"La receta no existe");}
 @ExceptionHandler(WebClientResponseException.class) ResponseEntity<Map<String,Object>> unavailable(){return error(503,"No fue posible validar la receta");}
 private ResponseEntity<Map<String,Object>> error(int s,String m){return ResponseEntity.status(s).body(Map.of("timestamp",Instant.now(),"status",s,"message",m));}
}

