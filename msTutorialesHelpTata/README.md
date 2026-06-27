# ms-Tutoriales

Microservicio de gestión de contenido educativo de HelpTata. Almacena los tutoriales (cursos) y sus fotos asociadas. El frontend consume este servicio para mostrar la lista de cursos disponibles.

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
- Base de datos `helptata_tutoriales` creada

```sql
CREATE DATABASE helptata_tutoriales;
```

---

## Configuración

```properties
server.port=8082
spring.datasource.url=jdbc:postgresql://localhost:5432/helptata_tutoriales
spring.datasource.username=postgres
spring.datasource.password=
```

---

## Ejecución

```bash
cd msTutorialesHelpTata
./mvnw spring-boot:run
```

El servicio queda disponible en `http://localhost:8082`.

---

## Endpoints disponibles

### Tutoriales — `/api/tutoriales`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/tutoriales` | Lista todos los tutoriales |
| GET | `/api/tutoriales/{id}` | Obtiene un tutorial por ID |
| GET | `/api/tutoriales/categoria/{categoria}` | Filtra tutoriales por categoría |
| GET | `/api/tutoriales/nivel/{nivel}` | Filtra tutoriales por nivel |
| POST | `/api/tutoriales` | Crea un tutorial nuevo |
| PUT | `/api/tutoriales/{id}` | Actualiza un tutorial |
| DELETE | `/api/tutoriales/{id}` | Elimina un tutorial |

### Fotos — `/api/fotos`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/fotos` | Lista todas las fotos |
| GET | `/api/fotos/{id}` | Obtiene una foto por ID |
| GET | `/api/fotos/tutorial/{idTutor}` | Fotos de un tutorial específico |
| POST | `/api/fotos` | Carga una foto nueva |
| PUT | `/api/fotos/{id}` | Actualiza una foto |
| DELETE | `/api/fotos/{id}` | Elimina una foto |

---

### Configuración — `/api/configuracion`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/configuracion/{clave}` | Obtiene el valor de una configuración por clave |

Claves disponibles en la base de datos:

| Clave | Valor por defecto | Descripción |
|---|---|---|
| `url_video_tutorial` | URL al video introductorio | URL usada por el frontend para el video de la sección "Conoce HelpTata" |

---

## Ejemplo de respuesta — Lista de tutoriales

```bash
curl http://localhost:8082/api/tutoriales
```

```json
[
  {
    "id_tutor": 1,
    "nombre_tuto": "Protección contra Estafas Digitales",
    "descripcion_tuto": "Aprende a identificar y evitar estafas en línea.",
    "tutorial": "https://www.youtube.com/embed/...",
    "categoria": "Seguridad",
    "nivel": "Básico"
  }
]
```

---

## Despliegue en producción

En producción este microservicio corre en un contenedor Docker. No es necesario instalar Java ni Maven en el servidor.

```bash
docker compose up --build ms-tutoriales -d
```

La URL de la base de datos se inyecta mediante `SPRING_DATASOURCE_URL` definida en el `docker-compose.yml`. Las URLs de videos e imágenes almacenadas en la BD deben apuntar a `helptata.cl:9000` y `helptata.cl:9001` respectivamente — actualizar con el script `docker/postgres/update_media_urls.sql` tras el primer arranque.

---

## Notas de implementación

### Entidad Configuracion

Los campos de la entidad `Configuracion` usan **camelCase** en Java con `@Column(name = "...")` para mapear a los nombres snake_case de la base de datos:

```java
@Column(name = "id_config")      private int idConfig;
@Column(name = "clave_config")   private String claveConfig;
@Column(name = "valor_config")   private String valorConfig;
```

Esto es necesario porque Spring Data JPA deriva los nombres de métodos del repositorio a partir del nombre del campo Java, no del nombre de la columna en base de datos. Usar snake_case en los campos Java causaría un error de arranque al no encontrar la propiedad.

---

## Integración con ms-Logs

Este servicio reporta eventos a `ms-Logs` (puerto 8081) de forma automática y fire-and-forget:

| Evento | Tipo de log |
|---|---|
| Tutorial creado | `INFO` |
| Tutorial actualizado | `INFO` |
| Tutorial eliminado | `INFO` |
| Foto agregada | `INFO` |
| Foto eliminada | `INFO` |
| Cualquier excepción no manejada | `ERROR` (vía `GlobalExceptionHandler`) |
| `ResponseStatusException` 4xx | `WARNING` (vía `GlobalExceptionHandler`) |

---

## Pruebas unitarias

Las pruebas se ubican en `src/test/java/com/Tutoriales/ms/TutorialServiceTest.java` y usan **JUnit 5 + Mockito**. No se necesita base de datos.

**Ejecutar:**
```bash
cd msTutorialesHelpTata
./mvnw test
```

| Test | Qué verifica |
|---|---|
| `obtenerTodosLosTutoriales_retornaLista` | Lista de tutoriales correcta |
| `obtenerTutorialPorId_existente` | DTO correcto para ID existente |
| `obtenerTutorialPorId_noExiste_lanza404` | HTTP 404 para ID inexistente |
| `obtenerTutorialesPorCategoria_filtrado` | Filtro por categoría funciona |
| `obtenerTutorialesPorNivel_filtrado` | Filtro por nivel funciona |
| `agregarTutorial_guardaCorrectamente` | Guardado y retorno del DTO |
| `actualizarTutorial_actualiza` | Actualización y retorno del DTO |
| `eliminarTutorial_existente` | Eliminación exitosa |
| `eliminarTutorial_noExiste_lanza404` | HTTP 404 al eliminar ID inexistente |
