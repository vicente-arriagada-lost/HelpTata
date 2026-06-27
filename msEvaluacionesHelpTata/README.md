# ms-Evaluaciones

Microservicio de gestión de evaluaciones formales de HelpTata. Administra las evaluaciones asociadas a los tutoriales, clasificadas por nivel y tipo. Es diferente de `ms-PreguntasRespuestas`: este servicio gestiona la estructura de las evaluaciones, mientras que el otro maneja los cuestionarios interactivos con preguntas y alternativas.

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
- Base de datos `helptata_evaluaciones` creada

```sql
CREATE DATABASE helptata_evaluaciones;
```

---

## Configuración

```properties
server.port=8085
spring.datasource.url=jdbc:postgresql://localhost:5432/helptata_evaluaciones
spring.datasource.username=postgres
spring.datasource.password=
```

---

## Ejecución

```bash
cd msEvaluacionesHelpTata
./mvnw spring-boot:run
```

El servicio queda disponible en `http://localhost:8085`.

---

## Endpoints disponibles

### Evaluaciones — `/api/evaluaciones`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/evaluaciones` | Lista todas las evaluaciones |
| GET | `/api/evaluaciones/{id}` | Obtiene una evaluación por ID |
| GET | `/api/evaluaciones/tutorial/{idTutor}` | Evaluaciones de un tutorial específico |
| GET | `/api/evaluaciones/nivel/{nivel}` | Filtra evaluaciones por nivel |
| GET | `/api/evaluaciones/tipo/{tipo}` | Filtra evaluaciones por tipo |
| POST | `/api/evaluaciones` | Crea una evaluación |
| PUT | `/api/evaluaciones/{id}` | Actualiza una evaluación |
| DELETE | `/api/evaluaciones/{id}` | Elimina una evaluación |

---

## Integración con ms-Logs

Este servicio reporta eventos a `ms-Logs` (puerto 8081) de forma automática y fire-and-forget:

| Evento | Tipo de log |
|---|---|
| Evaluación creada | `INFO` |
| Evaluación actualizada | `INFO` |
| Evaluación eliminada | `INFO` |
| Cualquier excepción no manejada | `ERROR` (vía `GlobalExceptionHandler`) |
| `ResponseStatusException` 4xx | `WARNING` (vía `GlobalExceptionHandler`) |

---

## Despliegue en producción

En producción este microservicio corre en un contenedor Docker. No es necesario instalar Java ni Maven en el servidor.

```bash
docker compose up --build ms-evaluaciones -d
```

La URL de la base de datos se inyecta mediante `SPRING_DATASOURCE_URL` definida en el `docker-compose.yml`.

---

## Pruebas unitarias

Las pruebas se ubican en `src/test/java/com/Evaluaciones/ms/EvaluacionServiceTest.java` y usan **JUnit 5 + Mockito**. No se necesita base de datos.

**Ejecutar:**
```bash
cd msEvaluacionesHelpTata
./mvnw test
```

| Test | Qué verifica |
|---|---|
| `obtenerTodasLasEvaluaciones_retornaLista` | Lista de evaluaciones correcta |
| `obtenerTodasLasEvaluaciones_listaVacia` | Lista vacía si no hay datos |
| `obtenerEvaluacionPorId_existente` | DTO correcto para ID existente |
| `obtenerEvaluacionPorId_noExiste_lanza404` | HTTP 404 para ID inexistente |
| `obtenerEvaluacionesPorTutorial_filtrado` | Filtro por id_tutor funciona |
| `obtenerEvaluacionesPorNivel_filtrado` | Filtro por nivel funciona |
| `obtenerEvaluacionesPorTipo_filtrado` | Filtro por tipo funciona |
| `agregarEvaluacion_guardaCorrectamente` | Guardado y retorno del DTO |
| `actualizarEvaluacion_actualiza` | Actualización y retorno del DTO |
| `actualizarEvaluacion_noExiste_lanza404` | HTTP 404 al actualizar ID inexistente |
| `eliminarEvaluacion_existente` | Eliminación exitosa |
| `eliminarEvaluacion_noExiste_lanza404` | HTTP 404 al eliminar ID inexistente |
