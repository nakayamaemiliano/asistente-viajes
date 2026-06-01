# Swagger / OpenAPI

Esta API expone documentacion interactiva con Swagger UI.

## URLs locales

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Autenticacion JWT

La mayoria de los endpoints requieren un token JWT.

1. Iniciar sesion:

```http
POST /auth/login
Content-Type: application/json
```

```json
{
  "email": "admin@test.com",
  "password": "123456"
}
```

2. Copiar el valor de `token` de la respuesta.

3. En Swagger UI, hacer click en `Authorize`.

4. Pegar el token con este formato:

```text
Bearer eyJhbGciOi...
```

5. Confirmar con `Authorize`.

## Usuarios de prueba

```text
admin@test.com  / 123456 / ADMIN
asesor@test.com / 123456 / ASESOR
lector@test.com / 123456 / LECTOR
```

## Permisos principales

```text
ADMIN
- Puede gestionar usuarios.
- Puede crear, modificar, consultar y eliminar clientes, destinos y consultas.

ASESOR
- Puede crear, modificar y consultar clientes.
- Puede crear, modificar y consultar consultas de viaje.
- Puede generar recomendaciones IA.
- Puede consultar destinos.
- No puede eliminar clientes, destinos, consultas ni usuarios.

LECTOR
- Puede consultar clientes, destinos y consultas.
- No puede crear, modificar, eliminar ni generar recomendaciones IA.
```

## Verificacion rapida

Con la app levantada, estas rutas deberian responder:

```text
GET /v3/api-docs
GET /swagger-ui/index.html
```

Si Swagger UI abre pero las requests protegidas devuelven `403`, revisar que se haya pegado el token desde `Authorize`.
