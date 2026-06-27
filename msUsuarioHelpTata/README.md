# ms-Usuario

Microservicio de gestión de usuarios de la plataforma HelpTata. Maneja el registro, autenticación y administración de los datos personales de los usuarios, así como sus roles y correos electrónicos.

---

## Tecnologías

| Herramienta | Versión | Para qué se usa |
|---|---|---|
| Java | 21 | Lenguaje principal |
| Spring Boot | 3 | Framework de aplicación |
| Spring Data JPA | — | Acceso a base de datos |
| PostgreSQL | 16 | Base de datos relacional |
| Maven | 3.9 | Gestor de dependencias |

---

## Requisitos previos

- Java 21 instalado (`java -version`)
- PostgreSQL corriendo en el puerto 5432
- Base de datos `helptata_usuario` creada

```sql
CREATE DATABASE helptata_usuario;
```

---

## Configuración

El archivo `src/main/resources/application.properties` contiene la configuración principal:

```properties
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/helptata_usuario
spring.datasource.username=postgres
spring.datasource.password=
```

Cambiar `spring.datasource.password` según la contraseña del usuario `postgres` local.

---

## Ejecución

```bash
cd msUsuarioHelpTata
./mvnw spring-boot:run
```

El servicio queda disponible en `http://localhost:8080`.

---

## Endpoints disponibles

### Usuarios — `/api/usuarios`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/usuarios` | Lista todos los usuarios |
| GET | `/api/usuarios/{id}` | Obtiene un usuario por ID |
| POST | `/api/usuarios` | Crea un nuevo usuario |
| PUT | `/api/usuarios/{id}` | Actualiza un usuario |
| DELETE | `/api/usuarios/{id}` | Elimina un usuario |
| POST | `/api/usuarios/login` | Autentica email + contraseña |

### Correos — `/api/emails`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/emails` | Lista todos los correos |
| GET | `/api/emails/{id}` | Obtiene un correo por ID |
| POST | `/api/emails` | Registra un correo |
| PUT | `/api/emails/{id}` | Actualiza un correo |
| DELETE | `/api/emails/{id}` | Elimina un correo |

### Roles — `/api/roles`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/roles` | Lista todos los roles |
| GET | `/api/roles/{id}` | Obtiene un rol por ID |
| POST | `/api/roles` | Crea un rol |
| PUT | `/api/roles/{id}` | Actualiza un rol |
| DELETE | `/api/roles/{id}` | Elimina un rol |

---

## Ejemplo de petición — Login

```bash
curl -X POST http://localhost:8080/api/usuarios/login \
  -H "Content-Type: application/json" \
  -d '{"email": "usuario@ejemplo.com", "password": "mi_clave"}'
```

Respuesta exitosa (el token debe guardarse para enviarlo en peticiones posteriores):
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "id": 1,
  "nombre": "Juan Pérez",
  "email": "usuario@ejemplo.com",
  "rol": "USER"
}
```

El campo `token` es un JWT firmado con HMAC-SHA. El frontend lo almacena en `localStorage` y lo adjunta en el header `Authorization: Bearer <token>` en cada petición posterior.

**Ejemplo de petición autenticada:**
```bash
curl http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

## Comunicación con otros microservicios

Este servicio llama a `ms-Direccion` (puerto 8084) al crear un usuario para asociar una dirección por defecto.

Reporta eventos a `ms-Logs` (puerto 8081) de forma automática y fire-and-forget:

| Evento | Tipo de log |
|---|---|
| Login exitoso | `AUTENTICACION` |
| Login fallido (credenciales incorrectas) | `WARNING` |
| Usuario creado | `INFO` |
| Usuario actualizado | `INFO` |
| Usuario eliminado | `INFO` |
| Cualquier excepción no manejada | `ERROR` (vía `GlobalExceptionHandler`) |
| `ResponseStatusException` 4xx | `WARNING` (vía `GlobalExceptionHandler`) |

---

## Despliegue en producción

En producción este microservicio corre en un contenedor Docker. No es necesario instalar Java ni Maven en el servidor.

```bash
docker compose up --build ms-usuario -d
```

La URL de la base de datos se inyecta mediante `SPRING_DATASOURCE_URL` definida en el `docker-compose.yml`.

---

## Notas de implementación

### Teléfono
El campo `telefono_usuario` es de tipo `String` (no `int`) para soportar el prefijo internacional y números largos. Formato esperado: `"+56912345678"`.

---

## Seguridad

- Las contraseñas se hashean con **BCrypt** antes de guardarse — nunca se almacena texto plano.
- Al iniciar el MS, `DataInitializer` migra automáticamente contraseñas en texto plano ya existentes en la BD.
- La autenticación usa **JWT (JSON Web Token)**. El token se incluye en el header `Authorization: Bearer <token>`.
- Endpoints públicos (no requieren token): `POST /api/usuarios/login`, `POST /api/usuarios`.

---

## Pruebas unitarias

Las pruebas se ubican en `src/test/java/com/Usuario/ms/UsuarioServiceTest.java` y usan **JUnit 5 + Mockito** (incluidos en `spring-boot-starter-test`). No se necesita base de datos: los repositorios son mocks.

**Ejecutar todas las pruebas:**
```bash
./mvnw test
```

**Ejecutar solo este microservicio:**
```bash
cd msUsuarioHelpTata
./mvnw test
```

### Casos cubiertos

| Test | Qué verifica |
|---|---|
| `obtenerTodosLosUsuarios_retornaLista` | Lista de usuarios no vacía |
| `obtenerTodosLosUsuarios_listaVacia` | Lista vacía cuando no hay datos |
| `obtenerUsuarioPorId_existente` | DTO correcto para ID existente |
| `obtenerUsuarioPorId_noExiste_lanza404` | HTTP 404 para ID inexistente |
| `agregarUsuario_hashea_password` | BCrypt se invoca al crear usuario |
| `agregarUsuario_asignaRolUser` | Rol por defecto = 2 (USER) |
| `eliminarUsuario_existente` | Eliminación exitosa |
| `eliminarUsuario_noExiste_lanza404` | HTTP 404 al eliminar ID inexistente |
| `login_credencialesIncorrectas_lanzaExcepcion` | Excepción con credenciales malas |
