# Asist Viajes API

API REST backend para una agencia de viajes. Permite gestionar clientes, destinos, consultas de viaje, usuarios del sistema y recomendaciones generadas con IA.

El proyecto integra seguridad JWT con roles, consumo de APIs publicas, persistencia con JPA/MySQL, documentacion OpenAPI/Swagger y una coleccion de Postman para validar permisos.

## Tecnologias

- Java 17
- Spring Boot 4.0.6
- Spring Web MVC
- Spring Data JPA
- Spring Validation
- Spring Security
- JWT con JJWT
- MySQL
- Lombok
- RestClient
- Jackson
- Springdoc OpenAPI / Swagger UI
- Maven

## Funcionalidades principales

- CRUD de clientes.
- CRUD de destinos.
- CRUD de consultas de viaje.
- CRUD de usuarios del sistema.
- Autenticacion con JWT.
- Autorizacion por roles: `ADMIN`, `ASESOR`, `LECTOR`.
- Consulta de informacion externa de paises con REST Countries.
- Consulta de clima actual con Open-Meteo.
- Generacion de recomendaciones inteligentes con Gemini.
- Persistencia de recomendaciones IA para evitar llamadas repetidas.
- Manejo centralizado de errores.
- Validaciones con DTOs y `@Valid`.
- Documentacion interactiva con Swagger UI.
- Coleccion Postman para probar seguridad y permisos.

## Modelo de dominio

Entidades principales:

```text
Cliente
- clienteId
- nombre
- apellido
- email
- telefono
- paisOrigen

Destino
- destinoId
- ciudad
- pais
- codigoPais
- moneda
- idiomaPrincipal
- region
- latitud
- longitud

ConsultaViaje
- consultaViajeId
- cliente
- destino
- fechaInicio
- fechaFin
- cantidadPersonas
- presupuesto
- intereses
- estado

RecomendacionIA
- recomendacionIaId
- consultaViaje
- resumen
- recomendaciones
- advertencias
- fechaGeneracion

Usuario
- usuarioId
- nombre
- email
- password
- rol
```

Enums:

```text
Rol: ADMIN, ASESOR, LECTOR
Estado: PENDIENTE, ANALIZADA, COTIZADA, CANCELADA
Presupuesto: BAJO, MEDIO, ALTO
```

## Reglas de negocio

- Una consulta de viaje no puede tener fecha de fin anterior a la fecha de inicio.
- La fecha de inicio no puede ser anterior a la fecha actual.
- La cantidad de personas debe ser como minimo 1.
- Al crear una consulta, el estado inicial es `PENDIENTE`.
- No se puede generar una recomendacion IA para una consulta `CANCELADA`.
- Cuando se genera una recomendacion IA, la consulta pasa a estado `ANALIZADA`.
- Si una consulta ya tiene recomendacion IA, se devuelve la recomendacion guardada y no se vuelve a llamar a Gemini.
- Las passwords de usuarios se guardan encriptadas con BCrypt.

## Seguridad

La API usa autenticacion JWT.

Endpoint de login:

```http
POST /auth/login
```

Body:

```json
{
  "email": "admin@test.com",
  "password": "123456"
}
```

Respuesta:

```json
{
  "token": "eyJhbGciOi...",
  "tipo": "Bearer",
  "email": "admin@test.com",
  "rol": "ADMIN"
}
```

El token se envia en cada request protegida:

```http
Authorization: Bearer eyJhbGciOi...
```

## Roles y permisos

| Recurso | ADMIN | ASESOR | LECTOR |
|---|---:|---:|---:|
| Usuarios | CRUD completo | No permitido | No permitido |
| Clientes | CRUD completo | Crear, listar, buscar y actualizar | Solo lectura |
| Destinos | CRUD completo | Solo lectura | Solo lectura |
| Consultas de viaje | CRUD completo | Crear, listar, buscar y actualizar | Solo lectura |
| Recomendaciones IA | Generar y consultar | Generar y consultar | Solo consultar |

## Endpoints principales

### Auth

| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/auth/login` | Iniciar sesion y obtener token JWT |

### Clientes

| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/clientes` | Listar clientes |
| POST | `/clientes` | Crear cliente |
| GET | `/clientes/{id}` | Buscar cliente por id |
| PUT | `/clientes/{id}` | Actualizar cliente |
| DELETE | `/clientes/{id}` | Eliminar cliente |

### Destinos

| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/destinos` | Listar destinos |
| POST | `/destinos` | Crear destino |
| GET | `/destinos/{id}` | Buscar destino por id |
| PUT | `/destinos/{id}` | Actualizar destino |
| DELETE | `/destinos/{id}` | Eliminar destino |
| GET | `/destinos/buscar-pais/{pais}` | Consultar informacion de pais con REST Countries |
| GET | `/destinos/{id}/clima` | Consultar clima actual del destino con Open-Meteo |

### Consultas de viaje

| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/consultas-viaje` | Listar consultas |
| POST | `/consultas-viaje` | Crear consulta |
| GET | `/consultas-viaje/{id}` | Buscar consulta por id |
| PUT | `/consultas-viaje/{id}` | Actualizar consulta |
| DELETE | `/consultas-viaje/{id}` | Eliminar consulta |

### Recomendaciones IA

| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/consultas-viaje/{id}/generar-recomendacion` | Generar recomendacion inteligente |
| GET | `/consultas-viaje/{id}/recomendacion` | Obtener recomendacion guardada |

### Usuarios

| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/usuarios` | Listar usuarios |
| POST | `/usuarios` | Crear usuario |
| GET | `/usuarios/{id}` | Buscar usuario por id |
| PUT | `/usuarios/{id}` | Actualizar usuario |
| DELETE | `/usuarios/{id}` | Eliminar usuario |

## Integraciones externas

### REST Countries

Usada para obtener informacion general de paises:

- nombre
- capital
- region
- codigo de pais
- monedas
- idiomas

Endpoint interno:

```http
GET /destinos/buscar-pais/{pais}
```

### Open-Meteo

Usada para obtener clima actual segun latitud y longitud del destino:

- temperatura
- velocidad del viento
- direccion del viento
- codigo de clima
- fecha/hora de medicion

Endpoint interno:

```http
GET /destinos/{id}/clima
```

### Gemini

Usada para generar una recomendacion estructurada de viaje a partir de:

- cliente
- destino
- fechas
- cantidad de personas
- presupuesto
- intereses

La respuesta de IA se procesa como JSON y se guarda en base de datos.

Ejemplo de respuesta:

```json
{
  "consultaViajeId": 3,
  "cliente": "Emiliano Nakayama",
  "destino": "Buenos Aires, Argentina",
  "resumen": "Viaje cultural y gastronomico para dos personas.",
  "recomendaciones": "Visitar San Telmo, Recoleta, Palermo y museos principales.",
  "advertencias": "Reservar con anticipacion y cuidar pertenencias en zonas turisticas.",
  "fechaGeneracion": "2026-05-30T12:50:29"
}
```

## Variables de entorno

La aplicacion necesita estas variables:

```text
GEMINI_API_KEY=tu_api_key_de_gemini
JWT_SECRET_KEY=clave_larga_para_firmar_tokens_jwt
```

Ejemplo en PowerShell:

```powershell
$env:GEMINI_API_KEY="tu_api_key_de_gemini"
$env:JWT_SECRET_KEY="mi-clave-super-secreta-para-jwt-de-minimo-48-caracteres"
```

## Base de datos

La configuracion actual usa MySQL local:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/asistente_viajes?useSSL=false&serverTimezone=America/Argentina/Buenos_Aires&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

Crear la base de datos:

```sql
CREATE DATABASE asistente_viajes;
```

## Como ejecutar el proyecto

1. Clonar el repositorio.

2. Crear la base de datos MySQL:

```sql
CREATE DATABASE asistente_viajes;
```

3. Configurar variables de entorno:

```powershell
$env:GEMINI_API_KEY="tu_api_key_de_gemini"
$env:JWT_SECRET_KEY="mi-clave-super-secreta-para-jwt-de-minimo-48-caracteres"
```

4. Ejecutar la aplicacion:

```powershell
.\mvnw spring-boot:run
```

5. Abrir Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

## Swagger / OpenAPI

La API esta documentada con Springdoc OpenAPI.

URLs locales:

```text
Swagger UI: http://localhost:8080/swagger-ui/index.html
OpenAPI JSON: http://localhost:8080/v3/api-docs
```

Guia adicional:

```text
docs/swagger.md
```

Para probar endpoints protegidos desde Swagger:

1. Ejecutar `POST /auth/login`.
2. Copiar el token.
3. Hacer click en `Authorize`.
4. Pegar el token con formato `Bearer <token>`.
5. Ejecutar requests protegidas.

## Postman

El proyecto incluye una coleccion Postman para probar seguridad y permisos:

```text
postman/asistente-viajes-security.postman_collection.json
```

La coleccion contiene:

- login de `ADMIN`, `ASESOR` y `LECTOR`
- guardado automatico de tokens
- pruebas de permisos por rol
- requests temporales para crear y eliminar datos de prueba

Usuarios de prueba usados en la coleccion:

```text
admin@test.com  / 123456 / ADMIN
asesor@test.com / 123456 / ASESOR
lector@test.com / 123456 / LECTOR
```

## Ejemplos de requests

### Crear cliente

```http
POST /clientes
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "nombre": "Lucia",
  "apellido": "Gomez",
  "email": "lucia.gomez@test.com",
  "telefono": "1122334455",
  "paisOrigen": "Argentina"
}
```

### Crear destino

```http
POST /destinos
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "ciudad": "Roma",
  "pais": "Italia",
  "codigoPais": "IT",
  "moneda": "EUR",
  "idiomaPrincipal": "Italiano",
  "region": "Europa",
  "latitud": 41.9028,
  "longitud": 12.4964
}
```

### Crear consulta de viaje

```http
POST /consultas-viaje
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "clienteId": 1,
  "destinoId": 1,
  "fechaInicio": "2026-07-10",
  "fechaFin": "2026-07-18",
  "cantidadPersonas": 2,
  "presupuesto": "MEDIO",
  "intereses": "museos, comida, arquitectura"
}
```

### Generar recomendacion IA

```http
POST /consultas-viaje/1/generar-recomendacion
Authorization: Bearer <token>
```

## Estructura del proyecto

```text
src/main/java/com/asistente_viajes_api/project
├── config
├── controller
├── dto
├── entity
├── enums
├── exception
├── external
│   ├── ai
│   ├── countries
│   └── weather
├── mapper
├── repository
├── security
└── service
```

## Estado actual

Implementado:

- CRUD de entidades principales.
- Seguridad JWT con roles.
- Integracion con REST Countries.
- Integracion con Open-Meteo.
- Integracion con Gemini para recomendaciones IA.
- Swagger/OpenAPI.
- Coleccion Postman de seguridad.

Proximas mejoras posibles:

- Agregar Dockerfile y Docker Compose.
- Sumar tests unitarios y de integracion.
- Mejorar respuesta de seguridad sin token de `403` a `401`.
- Agregar filtros de busqueda avanzados para consultas de viaje.
- Incorporar migraciones con Flyway o Liquibase.
- Preparar datos iniciales con seeders o scripts SQL.
