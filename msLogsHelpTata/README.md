# ms-Logs

Microservicio de registro de actividad de la plataforma HelpTata. Guarda trazas de las acciones de los usuarios (inicio de sesión, acceso a cursos, errores) para facilitar el monitoreo y la auditoría del sistema.

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
- Base de datos `helptata_logs` creada

```sql
CREATE DATABASE helptata_logs;
```

---

## Configuración

```properties
server.port=8081
spring.datasource.url=jdbc:postgresql://localhost:5432/helptata_logs
spring.datasource.username=postgres
spring.datasource.password=
```

---

## Ejecución

```bash
cd msLogsHelpTata
./mvnw spring-boot:run
```

El servicio queda disponible en `http://localhost:8081`.

---

## Endpoints disponibles

### Logs — `/api/logs`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/logs` | Lista todos los registros |
| GET | `/api/logs/{id}` | Obtiene un registro por ID |
| GET | `/api/logs/tipo/{tipo}` | Filtra por tipo de evento |
| GET | `/api/logs/usuario/{idUsuario}` | Registros de un usuario específico |
| GET | `/api/logs/servicio/{servicio}` | Registros de un microservicio específico |
| POST | `/api/logs` | Crea un nuevo registro de log |
| PUT | `/api/logs/{id}` | Actualiza un registro |
| DELETE | `/api/logs/{id}` | Elimina un registro |

---

## Integración automática con otros microservicios

Todos los microservicios del sistema cuentan con un `LogClient` que llama automáticamente a `POST /api/logs`. Las llamadas son **fire-and-forget**: si ms-Logs está caído, el MS que llama sigue funcionando sin interrupciones.

Cada MS también tiene un `GlobalExceptionHandler` (`@RestControllerAdvice`) que intercepta cualquier excepción no manejada y la envía aquí antes de responder al cliente.

| MS origen | Eventos registrados |
|---|---|
| `ms-usuario` | Login exitoso/fallido, usuario creado/actualizado/eliminado, errores HTTP |
| `ms-tutoriales` | Tutorial creado/actualizado/eliminado, foto agregada/eliminada, errores HTTP |
| `ms-progreso` | Progreso iniciado/actualizado, errores HTTP |
| `ms-evaluaciones` | Evaluación creada/actualizada/eliminada, errores HTTP |
| `ms-preguntas-respuestas` | Cuestionario/pregunta creado/eliminado, cuestionario completado (con puntaje), errores HTTP |
| `ms-direccion` | Errores HTTP |
| `ms-tatabot` | Errores HTTP |

---

## Despliegue en producción

En producción este microservicio corre en un contenedor Docker. Debe arrancar **antes** que cualquier otro MS ya que todos dependen de él para registrar eventos.

```bash
docker compose up --build ms-logs -d
```

La URL de la base de datos se inyecta mediante `SPRING_DATASOURCE_URL` definida en el `docker-compose.yml`.

---

## Ejemplo de petición — Registrar un log

```bash
curl -X POST http://localhost:8081/api/logs \
  -H "Content-Type: application/json" \
  -d '{
    "tipo_log": "INFO",
    "servicio_origen": "ms-usuario",
    "mensaje_log": "Usuario inició sesión correctamente",
    "id_usuario": 1,
    "ip_log": "192.168.1.10",
    "detalle_log": "Login exitoso desde navegador Chrome"
  }'
```

**Tipos de log válidos:** `ERROR`, `WARNING`, `INFO`, `DEBUG`, `AUTENTICACION`

---

## Pruebas unitarias

Las pruebas se ubican en `src/test/java/com/Logs/ms/LogServiceTest.java` y usan **JUnit 5 + Mockito**. No se necesita base de datos.

**Ejecutar:**
```bash
cd msLogsHelpTata
./mvnw test
```

| Test | Qué verifica |
|---|---|
| `obtenerTodosLosLogs_retornaLista` | Lista de logs correcta |
| `obtenerLogPorId_existente` | DTO correcto para ID existente |
| `obtenerLogPorId_noExiste_lanza404` | HTTP 404 para ID inexistente |
| `obtenerLogsPorTipo_filtrado` | Filtro por tipo (ERROR, INFO, etc.) funciona |
| `obtenerLogsPorUsuario_filtrado` | Filtro por id_usuario funciona |
| `agregarLog_asignaFechaAutomatica` | `fecha_hora_log` es asignada por el servicio, no por el request |
| `eliminarLog_existente` | Eliminación exitosa |
| `eliminarLog_noExiste_lanza404` | HTTP 404 al eliminar ID inexistente |
| `actualizarLog_conservaFechaOriginal` | El timestamp original no se modifica al actualizar |
