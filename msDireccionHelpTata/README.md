# ms-Direccion

Microservicio de gestión de ubicaciones geográficas de HelpTata. Almacena regiones, comunas y direcciones de los usuarios. Es consultado por `ms-Usuario` al registrar un nuevo usuario para asignarle una dirección.

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
- Base de datos `helptata_direccion` creada

```sql
CREATE DATABASE helptata_direccion;
```

---

## Configuración

```properties
server.port=8084
spring.datasource.url=jdbc:postgresql://localhost:5432/helptata_direccion
spring.datasource.username=postgres
spring.datasource.password=
ms.logs.url=http://localhost:8081
```

---

## Ejecución

```bash
cd msDireccionHelpTata
./mvnw spring-boot:run
```

El servicio queda disponible en `http://localhost:8084`.

---

## Endpoints disponibles

La jerarquía geográfica es: **País → Región → Ciudad → Comuna → Dirección**. Se debe respetar ese orden al crear datos.

### Países — `/api/paises`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/paises` | Lista todos los países |
| GET | `/api/paises/{id}` | Obtiene un país por ID |
| POST | `/api/paises` | Crea un país |
| PUT | `/api/paises/{id}` | Actualiza un país |
| DELETE | `/api/paises/{id}` | Elimina un país |

### Regiones — `/api/regiones`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/regiones` | Lista todas las regiones |
| GET | `/api/regiones/{id}` | Obtiene una región por ID |
| GET | `/api/regiones/pais/{idPais}` | Regiones de un país |
| POST | `/api/regiones` | Crea una región |
| PUT | `/api/regiones/{id}` | Actualiza una región |
| DELETE | `/api/regiones/{id}` | Elimina una región |

### Ciudades — `/api/ciudades`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/ciudades` | Lista todas las ciudades |
| GET | `/api/ciudades/{id}` | Obtiene una ciudad por ID |
| GET | `/api/ciudades/region/{idRegion}` | Ciudades de una región |
| POST | `/api/ciudades` | Crea una ciudad |
| PUT | `/api/ciudades/{id}` | Actualiza una ciudad |
| DELETE | `/api/ciudades/{id}` | Elimina una ciudad |

### Comunas — `/api/comunas`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/comunas` | Lista todas las comunas |
| GET | `/api/comunas/{id}` | Obtiene una comuna por ID |
| GET | `/api/comunas/ciudad/{idCiudad}` | Comunas de una ciudad |
| POST | `/api/comunas` | Crea una comuna |
| PUT | `/api/comunas/{id}` | Actualiza una comuna |
| DELETE | `/api/comunas/{id}` | Elimina una comuna |

### Direcciones — `/api/direcciones`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/direcciones` | Lista todas las direcciones |
| GET | `/api/direcciones/{id}` | Obtiene una dirección por ID (consumido por ms-Usuario) |
| GET | `/api/direcciones/comuna/{idComuna}` | Filtra direcciones por comuna |
| POST | `/api/direcciones` | Crea una dirección |
| PUT | `/api/direcciones/{id}` | Actualiza una dirección |
| DELETE | `/api/direcciones/{id}` | Elimina una dirección |

---

## Notas

Este servicio es llamado internamente por `ms-Usuario` (puerto 8080) para validar que el `id_direccion` exista antes de asociarlo a un usuario. Ambos deben estar corriendo simultáneamente para que el registro de usuarios funcione correctamente.

Reporta errores a `ms-Logs` (puerto 8081) de forma fire-and-forget vía `GlobalExceptionHandler`: las excepciones no manejadas se registran como `ERROR` y las `ResponseStatusException` 4xx como `WARNING`.

---

## Despliegue en producción

En producción este microservicio corre en un contenedor Docker. No es necesario instalar Java ni Maven en el servidor.

```bash
# Desde la raíz del proyecto HelpTata
docker compose up --build ms-direccion -d
```

La imagen se construye automáticamente con Maven en etapa de build y JRE 21 Alpine en runtime. La URL de la base de datos se inyecta mediante la variable de entorno `SPRING_DATASOURCE_URL` definida en el `docker-compose.yml`.

---

## Pruebas unitarias

Las pruebas se ubican en `src/test/java/com/Direccion/ms/DireccionServiceTest.java` y usan **JUnit 5 + Mockito**. No se necesita base de datos.

**Ejecutar:**
```bash
cd msDireccionHelpTata
./mvnw test
```

| Test | Qué verifica |
|---|---|
| `obtenerTodasLasDirecciones_retornaLista` | Lista de direcciones correcta |
| `obtenerDireccionPorId_existente` | DTO correcto para ID existente |
| `obtenerDireccionPorId_noExiste_lanza404` | HTTP 404 para ID inexistente |
| `agregarDireccion_comunaExistente` | Guardado cuando la comuna existe |
| `agregarDireccion_comunaInexistente_lanza404` | HTTP 404 si la comuna no existe |
| `actualizarDireccion_actualiza` | Actualización exitosa |
| `actualizarDireccion_noExiste_lanza404` | HTTP 404 al actualizar ID inexistente |
| `eliminarDireccion_existente` | Eliminación exitosa |
| `eliminarDireccion_noExiste_lanza404` | HTTP 404 al eliminar ID inexistente |
| `obtenerDireccionesPorComuna_filtrado` | Filtro por id_comuna funciona |
