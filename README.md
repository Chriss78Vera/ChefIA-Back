# ChefIA - Backend de microservicios

Backend que recomienda recetas segun el animo y las preferencias alimenticias del usuario.

## Arquitectura

| Componente | Responsabilidad |
|---|---|
| `auth-svc` | Login y creacion administrativa de identidades en Keycloak |
| `usuarios-svc` | Perfil y preferencias ligadas al `sub` del JWT |
| `recetas-svc` | Catalogo y filtros; escritura solo para `ADMIN` |
| `favoritos-svc` | Favoritos por usuario; valida recetas mediante WebClient |
| `recomendaciones-svc` | Orquesta usuarios, recetas y Ollama con WebClient y Resilience4j |
| Keycloak | Identidades, emision de JWT y roles |
| Nginx | Unico punto de entrada en `http://localhost:8080` |

Cada servicio con estado tiene su propia base PostgreSQL. Ninguna tabla se comparte.

## Inicio

1. Asegurese de que Ollama este activo y descargue un modelo:
   `ollama pull llama3.2`
2. Copie `.env.example` a `.env` y cambie las contrasenias.
3. Ejecute:

```bash
docker compose up -d --build
```

Keycloak queda en `http://localhost:8180`; la API solo se publica mediante Nginx en
`http://localhost:8080`. El realm se importa automaticamente.

## Abrir el proyecto en IntelliJ IDEA

El proyecto requiere **JDK 17**.

1. Abra la carpeta raiz `ChefIA - BackendServices`, no solamente `src/`.
2. En `File > Project Structure > Project`, seleccione un JDK 17 y Language level 17.
3. En `Settings > Build Tools > Maven > Runner`, seleccione el mismo JDK 17.
4. Abra el `pom.xml` raiz y seleccione `Add as Maven Project` o `Reload All Maven Projects`.
5. Confirme que aparecen cinco modulos: `auth-svc`, `usuarios-svc`, `recetas-svc`,
   `favoritos-svc` y `recomendaciones-svc`.

Cada microservicio tambien tiene un POM Spring Boot autonomo y puede abrirse por
separado, siguiendo la misma estructura de los proyectos de referencia. IntelliJ
incluye Maven integrado; ademas, la raiz incluye `mvnw` y `mvnw.cmd` con Maven
3.9.9, por lo que no es obligatorio instalar `mvn` globalmente.

Usuarios de demostracion: `usuario/usuario123` y `admin/admin123`. Son unicamente
para sustentacion local; eliminelos o cambie sus claves fuera de desarrollo.

## Registro publico

```bash
curl -X POST http://localhost:8080/api/auth/registro \
  -H "Content-Type: application/json" \
  -d '{"username":"nuevo.usuario","email":"nuevo.usuario@chefia.local","nombre":"Nuevo","apellido":"Usuario","password":"ClaveSegura123!"}'
```

El registro no necesita token y asigna siempre el rol `USUARIO`. El cuerpo no
acepta roles, por lo que esta ruta no puede crear administradores.

## Iniciar sesion

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"usuario","password":"usuario123"}'
```

Use `Authorization: Bearer <access_token>` en todas las rutas `/api/**`.

## Alta administrativa de usuarios

El formulario de registro directo de Keycloak esta deshabilitado. El backend ofrece
`POST /api/auth/registro` para cuentas normales. Ademas, un token con rol `ADMIN`
puede crear cuentas mediante:

```http
POST /api/admin/usuarios
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "username": "nuevo.usuario",
  "email": "nuevo.usuario@chefia.local",
  "nombre": "Nuevo",
  "apellido": "Usuario",
  "password": "ClaveSegura123!"
}
```

Los usuarios creados por ADMIN reciben una clave temporal. El frontend debe solicitar el cambio antes del login:

```http
POST /api/auth/contrasenia-temporal
```

```json
{
  "username": "nuevo.usuario",
  "contraseniaTemporal": "ClaveSegura123!",
  "contraseniaNueva": "ClaveDefinitiva456!",
  "confirmacionContrasenia": "ClaveDefinitiva456!"
}
```

Un usuario con sesion activa puede cambiarla con `PUT /api/auth/contrasenia` enviando su token, la clave actual, la nueva y su confirmacion.

La cuenta se crea en Keycloak con rol `USUARIO`. En el primer acceso del usuario,
`usuarios-svc` crea su perfil complementario a partir del JWT. La contrasenia no se
almacena en las bases de datos de ChefIA. Un usuario sin rol `ADMIN` recibe
`403 Forbidden`. Ninguna de las dos rutas permite asignar el rol `ADMIN` a la
cuenta creada.

## Flujo principal

```bash
curl -X PUT http://localhost:8080/api/usuarios/preferencias \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"tipoAlimentacion":"VEGANO","restricciones":["SIN_LACTOSA"],"ingredientesNoDeseados":["mani"]}'

curl -X POST http://localhost:8080/api/recomendar \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"animo":"ESTRESADO","tipoReceta":"SOPA"}'
```

Cada recomendacion devuelve una receta completa: nombre, motivo, descripcion,
porciones, tiempo, dificultad, ingredientes con cantidades, pasos ordenados y
entre dos y cinco `tags` generados por Ollama. Por ejemplo:

```json
{
  "nombre": "Sopa de lentejas",
  "motivo": "Reconfortante para un momento de estres",
  "descripcion": "Sopa vegetal caliente y nutritiva",
  "porciones": 2,
  "tiempoMinutos": 30,
  "dificultad": "FACIL",
  "ingredientes": [
    "200 g de lentejas cocidas",
    "1 zanahoria",
    "1/2 cebolla"
  ],
  "pasos": [
    "Picar la zanahoria y la cebolla.",
    "Sofreir la cebolla durante 3 minutos.",
    "Agregar las lentejas y cocinar durante 20 minutos."
  ],
  "tags": ["vegano", "reconfortante", "saludable"]
}
```

Las pruebas manuales de la API se realizan con la coleccion de Postman incluida
en la carpeta `postman/`. Los health checks internos usan Actuator.

## Resiliencia demostrable

`recomendaciones-svc -> recetas-svc` aplica Circuit Breaker, Retry y TimeLimiter.
Si se detiene `recetas-svc`, se usan tres recetas de respaldo. La llamada a Ollama
tiene Circuit Breaker y Retry, pero no tiene fallback de contenido: si Ollama no
responde, la solicitud termina con error.

`favoritos-svc -> recetas-svc` aplica Circuit Breaker y Retry. Si recetas no esta
disponible, el favorito se guarda con un nombre provisional. Los errores `4xx` no
activan ese fallback. Los eventos se consultan en:

`GET /actuator/circuitbreakerevents`

La documentacion detallada esta disponible en `docs/README.md`.

## Pruebas

```bash
./mvnw test
# Windows
mvnw.cmd test
docker compose config
docker compose up -d --build
docker compose ps
```

En Windows puede usarse Maven instalado (`mvn test`) o los wrappers de cada proyecto.

### Postman

Importe los dos archivos de la carpeta `postman/`:

- `ChefIA.postman_collection.json`
- `ChefIA.postman_environment.json`

Seleccione el entorno **ChefIA - Local** y ejecute primero `01 - Autenticacion`.
Los scripts almacenan automaticamente `usuarioToken`, `adminToken` y el ID de la
receta administrativa creada.

El password grant de la coleccion se incluye exclusivamente para pruebas locales.
El cliente web debe iniciar sesion mediante Authorization Code + PKCE contra
Keycloak y enviar el access token como `Authorization: Bearer <token>`.
