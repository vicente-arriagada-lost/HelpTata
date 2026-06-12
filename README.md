# HelpTata — Backend (Microservicios)

Plataforma educativa de tutoriales construida con una arquitectura de **7 microservicios Spring Boot** independientes, cada uno con su propia base de datos H2 en memoria para desarrollo.

---

## Tabla de Contenidos

- [Requisitos previos](#requisitos-previos)
- [Cómo levantar los microservicios](#cómo-levantar-los-microservicios)
- [Descripción de cada microservicio](#descripción-de-cada-microservicio)
  - [ms-Usuario (8080)](#ms-usuario--puerto-8080)
  - [ms-Logs (8081)](#ms-logs--puerto-8081)
  - [ms-Tutoriales (8082)](#ms-tutoriales--puerto-8082)
  - [ms-Progreso (8083)](#ms-progreso--puerto-8083)
  - [ms-Direccion (8084)](#ms-dirección--puerto-8084)
  - [ms-Evaluaciones (8085)](#ms-evaluaciones--puerto-8085)
  - [ms-PreguntasRespuestas (8086)](#ms-preguntasrespuestas--puerto-8086)
- [Solicitudes con Postman](#solicitudes-con-postman)
- [Consolas H2](#consolas-h2)

---

## Requisitos previos

- **Java 21** (LTS)
- **Maven 3.8+**
- IDE recomendado: IntelliJ IDEA o Spring Tool Suite

Verificar versión de Java:
```bash
java -version
```

---

## Cómo levantar los microservicios

Cada microservicio es un proyecto Maven independiente. Se deben levantar en terminales separadas.

### Orden recomendado de inicio

Levantar **ms-Dirección primero** (ms-Usuario lo consume) y **ms-Progreso antes que ms-PreguntasRespuestas** (este último lo notifica al corregir un cuestionario).

```
1. ms-Direccion           (puerto 8084)
2. ms-Usuario             (puerto 8080)
3. ms-Tutoriales          (puerto 8082)
4. ms-Evaluaciones        (puerto 8085)
5. ms-Progreso            (puerto 8083)
6. ms-PreguntasRespuestas (puerto 8086)
7. ms-Logs                (puerto 8081)
```

### Comandos por microservicio

Abrir una terminal por cada microservicio y ejecutar:

**Terminal 1 — ms-Dirección**
```bash
cd msDireccionHelpTata
mvn spring-boot:run
```

**Terminal 2 — ms-Usuario**
```bash
cd msUsuarioHelpTata
mvn spring-boot:run
```

**Terminal 3 — ms-Tutoriales**
```bash
cd msTutorialesHelpTata
mvn spring-boot:run
```

**Terminal 4 — ms-Evaluaciones**
```bash
cd msEvaluacionesHelpTata
mvn spring-boot:run
```

**Terminal 5 — ms-Progreso**
```bash
cd msProgresoHelpTata
mvn spring-boot:run
```

**Terminal 6 — ms-PreguntasRespuestas**
```bash
cd msPreguntasYRespuestasHelpTata
mvn spring-boot:run
```

**Terminal 7 — ms-Logs**
```bash
cd msLogsHelpTata
mvn spring-boot:run
```

### Verificar que un microservicio está corriendo

Cada MS confirma que está activo con un mensaje similar a:
```
Started UsuarioApplication in X.XXX seconds
```

También se puede probar con un GET en su endpoint raíz desde el navegador o Postman.

---

## Descripción de cada microservicio

---

### ms-Usuario — Puerto 8080

Gestión de usuarios de la plataforma: creación, roles y emails asociados.

**Tecnología:** Spring Boot 3.3.5 · H2 · Lombok · Validation

**Comunicación:** Llama a **ms-Dirección (8084)** vía RestTemplate para validar que el `id_direccion` exista antes de asociarlo a un usuario.

**Entidades:**

| Entidad | Campos principales |
|---|---|
| `Usuario` | `id_usuario`, `run_usuario`, `dvrun_usuario`, `pnombre_usuario`, `snombre_usuario`, `papellido_usuario`, `sapellido_usuario`, `fecha_nac_usuario`, `telefono_usuario`, `password_usuario`, `fecha_reg_usuario`, `id_direccion` |
| `Rol` | `id_rol`, `tipo_rol` (ADMIN, USER, MODERATOR) |
| `Email` | `id_email`, `email` |

**Endpoints disponibles:**

```
GET    /api/usuarios          → Lista todos los usuarios
GET    /api/usuarios/{id}     → Obtiene usuario por ID
POST   /api/usuarios          → Crea un nuevo usuario
PUT    /api/usuarios/{id}     → Actualiza un usuario
DELETE /api/usuarios/{id}     → Elimina un usuario

GET    /api/roles             → Lista todos los roles
GET    /api/roles/{id}        → Obtiene rol por ID
POST   /api/roles             → Crea un nuevo rol
PUT    /api/roles/{id}        → Actualiza un rol
DELETE /api/roles/{id}        → Elimina un rol

GET    /api/emails            → Lista todos los emails
GET    /api/emails/{id}       → Obtiene email por ID
POST   /api/emails            → Registra un nuevo email
PUT    /api/emails/{id}       → Actualiza un email
DELETE /api/emails/{id}       → Elimina un email
```

---

### ms-Logs — Puerto 8081

Registro centralizado de eventos y errores generados por los microservicios del sistema.

**Tecnología:** Spring Boot 3.3.5 · H2 · Actuator · Lombok · Validation

**Entidades:**

| Entidad | Campos principales |
|---|---|
| `Log` | `id_log`, `tipo_log`, `servicio_origen`, `mensaje_log`, `fecha_hora_log`, `id_usuario`, `ip_log`, `detalle_log` |

**Tipos de log:** `ERROR`, `WARNING`, `INFO`, `DEBUG`, `AUTENTICACION`

**Servicios de origen de ejemplo:** `ms-usuario`, `ms-auth`, `ms-tutoriales`

**Endpoints disponibles:**

```
GET    /api/logs                        → Lista todos los logs
GET    /api/logs/{id}                   → Obtiene log por ID
GET    /api/logs/tipo/{tipo}            → Filtra por tipo (ERROR, WARNING, INFO, DEBUG, AUTENTICACION)
GET    /api/logs/usuario/{idUsuario}    → Logs de un usuario específico
GET    /api/logs/servicio/{servicio}    → Logs de un microservicio específico
POST   /api/logs                        → Registra un nuevo log
PUT    /api/logs/{id}                   → Actualiza un log
DELETE /api/logs/{id}                   → Elimina un log
```

**Actuator (monitoreo):**
```
GET /actuator/health   → Estado del microservicio
GET /actuator/info     → Información general
```

---

### ms-Tutoriales — Puerto 8082

Gestión del catálogo de tutoriales y sus recursos multimedia (fotos).

**Tecnología:** Spring Boot 4.0.6 · H2 · Lombok · Validation · Security

**Entidades:**

| Entidad | Campos principales |
|---|---|
| `Tutorial` | `id_tutor`, `nombre_tuto`, `cat_tuto`, `nivel_tuto`, `tutorial` (URL), `tiempo_tutorial` (minutos), `descripcion_tuto` |
| `Foto` | `id_foto`, `foto` (URL/base64), `id_tutor` (FK) |

**Niveles:** `BASICO`, `INTERMEDIO`, `AVANZADO`

**Categorías de ejemplo:** Programación, Diseño, Matemáticas

**Endpoints disponibles:**

```
GET    /api/fotos                        → Lista todas las fotos
GET    /api/fotos/{id}                   → Obtiene foto por ID
GET    /api/fotos/tutorial/{idTutor}     → Galería de fotos de un tutorial
POST   /api/fotos                        → Agrega una foto
PUT    /api/fotos/{id}                   → Actualiza una foto
DELETE /api/fotos/{id}                   → Elimina una foto
```

---

### ms-Progreso — Puerto 8083

Seguimiento del avance de cada usuario dentro de los tutoriales: recursos completados, preguntas respondidas y porcentaje general.

**Tecnología:** Spring Boot 4.0.6 · H2 · Lombok · Validation · Security

**Entidades:**

| Entidad | Campos principales |
|---|---|
| `Progreso` | `id_progreso`, `id_usuario` (FK), `id_tutorial` (FK), `recursos_completados`, `cantidad_recursos_totales`, `preguntas_acertadas`, `preguntas_falladas`, `porcentaje_progreso` (calculado), `fecha_ultima_actividad` |

> El campo `porcentaje_progreso` se recalcula automáticamente al actualizar el registro.

**Endpoints disponibles:**

```
GET    /api/progreso                                           → Lista todos los registros de progreso
GET    /api/progreso/{id}                                      → Obtiene progreso por ID
GET    /api/progreso/usuario/{idUsuario}                       → Todo el progreso de un usuario
GET    /api/progreso/tutorial/{idTutorial}                     → Progreso de todos los usuarios en un tutorial
GET    /api/progreso/usuario/{idUsuario}/tutorial/{idTutorial} → Progreso de un usuario en un tutorial específico
POST   /api/progreso                                           → Registra progreso inicial
PUT    /api/progreso/{id}                                      → Actualiza progreso
DELETE /api/progreso/{id}                                      → Elimina un registro de progreso
```

---

### ms-Dirección — Puerto 8084

Gestión de ubicaciones geográficas con estructura jerárquica: País → Región → Ciudad → Comuna → Dirección.

**Tecnología:** Spring Boot 4.0.6 · H2 · Lombok · Validation · Security

**Entidades:**

| Entidad | Campos principales |
|---|---|
| `Pais` | `id_pais`, `nombre_pais` |
| `Region` | `id_region`, `nombre_region`, `id_pais` (FK) |
| `Ciudad` | `id_ciudad`, `nombre_ciudad`, `id_region` (FK) |
| `Comuna` | `id_comuna`, `nombre_comuna`, `id_ciudad` (FK) |
| `Direccion` | `id_direccion`, `calle`, `numero`, `id_comuna` (FK) |

**Endpoints disponibles:**

```
GET    /api/paises                        → Lista todos los países
GET    /api/paises/{id}                   → Obtiene país por ID
POST   /api/paises                        → Crea un país
PUT    /api/paises/{id}                   → Actualiza un país
DELETE /api/paises/{id}                   → Elimina un país

GET    /api/regiones                      → Lista todas las regiones
GET    /api/regiones/{id}                 → Obtiene región por ID
GET    /api/regiones/pais/{idPais}        → Regiones de un país
POST   /api/regiones                      → Crea una región
PUT    /api/regiones/{id}                 → Actualiza una región
DELETE /api/regiones/{id}                 → Elimina una región

GET    /api/ciudades                      → Lista todas las ciudades
GET    /api/ciudades/{id}                 → Obtiene ciudad por ID
GET    /api/ciudades/region/{idRegion}    → Ciudades de una región
POST   /api/ciudades                      → Crea una ciudad
PUT    /api/ciudades/{id}                 → Actualiza una ciudad
DELETE /api/ciudades/{id}                 → Elimina una ciudad

GET    /api/comunas                       → Lista todas las comunas
GET    /api/comunas/{id}                  → Obtiene comuna por ID
GET    /api/comunas/ciudad/{idCiudad}     → Comunas de una ciudad
POST   /api/comunas                       → Crea una comuna
PUT    /api/comunas/{id}                  → Actualiza una comuna
DELETE /api/comunas/{id}                  → Elimina una comuna

GET    /api/direcciones                   → Lista todas las direcciones
GET    /api/direcciones/{id}              → Obtiene dirección por ID (consumido por ms-Usuario)
GET    /api/direcciones/comuna/{idComuna} → Direcciones de una comuna
POST   /api/direcciones                   → Crea una dirección
PUT    /api/direcciones/{id}              → Actualiza una dirección
DELETE /api/direcciones/{id}              → Elimina una dirección
```

---

### ms-Evaluaciones — Puerto 8085

Gestión del banco de evaluaciones asociadas a tutoriales, con soporte para distintos tipos y niveles de dificultad.

**Tecnología:** Spring Boot 4.0.6 · H2 · Lombok · Validation · Security

**Entidades:**

| Entidad | Campos principales |
|---|---|
| `Evaluacion` | `id_eva`, `nombre_eva`, `tipo_eva`, `nivel_eva`, `banco_preg` (cantidad preguntas), `id_tutor` (FK) |

**Tipos:** `QUIZ`, `EXAMEN`, `PRACTICA`, `AUTOEVALUACION`

**Niveles:** `BASICO`, `INTERMEDIO`, `AVANZADO`

**Endpoints disponibles:**

```
GET    /api/evaluaciones                      → Lista todas las evaluaciones
GET    /api/evaluaciones/{id}                 → Obtiene evaluación por ID
GET    /api/evaluaciones/tutorial/{idTutor}   → Evaluaciones de un tutorial
GET    /api/evaluaciones/nivel/{nivel}        → Filtra por nivel (BASICO, INTERMEDIO, AVANZADO)
GET    /api/evaluaciones/tipo/{tipo}          → Filtra por tipo (QUIZ, EXAMEN, PRACTICA, AUTOEVALUACION)
POST   /api/evaluaciones                      → Crea una evaluación
PUT    /api/evaluaciones/{id}                 → Actualiza una evaluación
DELETE /api/evaluaciones/{id}                 → Elimina una evaluación
```

---

### ms-PreguntasRespuestas — Puerto 8086

Gestión completa de cuestionarios: preguntas, alternativas, corrección automática de respuestas y registro de resultados por usuario. Es el motor de evaluación de la plataforma.

**Tecnología:** Spring Boot 4.0.6 · H2 · Lombok · Validation · Security

**Comunicación:** Al corregir un cuestionario, llama a **ms-Progreso (8083)** via RestTemplate para actualizar las preguntas acertadas/falladas del usuario en el tutorial correspondiente.

**Entidades:**

| Entidad | Campos principales |
|---|---|
| `Cuestionario` | `id_cuestionario`, `titulo_cuestionario`, `descripcion_cuestionario`, `id_tutor` (FK a ms-Tutoriales) |
| `Pregunta` | `id_pregunta`, `enunciado_pregunta`, `id_cuestionario` (FK) |
| `Alternativa` | `id_alternativa`, `texto_alternativa`, `es_correcta`, `id_pregunta` (FK) |
| `RespuestaUsuario` | `id_respuesta`, `id_usuario`, `id_pregunta`, `id_alternativa_seleccionada`, `id_resultado` (FK) |
| `ResultadoCuestionario` | `id_resultado`, `id_usuario`, `id_cuestionario`, `correctas`, `incorrectas`, `porcentaje`, `fecha_resultado` |

**Endpoints disponibles:**

```
GET    /api/cuestionarios                                           → Lista todos los cuestionarios
GET    /api/cuestionarios/{id}                                      → Obtiene cuestionario por ID
GET    /api/cuestionarios/tutorial/{idTutor}                        → Cuestionarios de un tutorial
POST   /api/cuestionarios                                           → Crea un cuestionario
PUT    /api/cuestionarios/{id}                                      → Actualiza un cuestionario
DELETE /api/cuestionarios/{id}                                      → Elimina un cuestionario
POST   /api/cuestionarios/{id}/responder                            → Envía respuestas, corrige y guarda resultado ★

GET    /api/preguntas                                               → Lista todas las preguntas
GET    /api/preguntas/{id}                                          → Obtiene pregunta por ID
GET    /api/preguntas/cuestionario/{idCuestionario}                 → Preguntas de un cuestionario
POST   /api/preguntas                                               → Crea una pregunta
PUT    /api/preguntas/{id}                                          → Actualiza una pregunta
DELETE /api/preguntas/{id}                                          → Elimina una pregunta

GET    /api/alternativas                                            → Lista todas las alternativas
GET    /api/alternativas/{id}                                       → Obtiene alternativa por ID
GET    /api/alternativas/pregunta/{idPregunta}                      → Alternativas de una pregunta
POST   /api/alternativas                                            → Crea una alternativa
PUT    /api/alternativas/{id}                                       → Actualiza una alternativa
DELETE /api/alternativas/{id}                                       → Elimina una alternativa

GET    /api/resultados                                              → Lista todos los resultados
GET    /api/resultados/{id}                                         → Obtiene resultado por ID
GET    /api/resultados/usuario/{idUsuario}                          → Historial de un usuario
GET    /api/resultados/cuestionario/{idCuestionario}                → Resultados de un cuestionario
GET    /api/resultados/usuario/{idUsuario}/cuestionario/{id}        → Historial de usuario en un cuestionario
DELETE /api/resultados/{id}                                         → Elimina un resultado
```

> ★ El endpoint `/responder` es el flujo principal: recibe las respuestas del usuario, verifica cada alternativa, calcula el puntaje, guarda el resultado, notifica a ms-Progreso y devuelve `{ correctas, incorrectas, porcentaje }`.

---

## Solicitudes con Postman

### Configuración general

En Postman, todas las solicitudes que envíen o reciban JSON deben tener el header:

```
Content-Type: application/json
```

---

### Ejemplos por microservicio

#### ms-Usuario (8080)

**Crear un usuario**
```
POST http://localhost:8080/api/usuarios
Content-Type: application/json

{
  "run_usuario": 12345678,
  "dvrun_usuario": "9",
  "pnombre_usuario": "Juan",
  "snombre_usuario": "Andrés",
  "papellido_usuario": "González",
  "sapellido_usuario": "Pérez",
  "fecha_nac_usuario": "2000-05-15",
  "telefono_usuario": "+56912345678",
  "password_usuario": "clave123",
  "id_direccion": 1
}
```

**Crear un rol**
```
POST http://localhost:8080/api/roles
Content-Type: application/json

{
  "tipo_rol": "USER"
}
```

**Crear un email**
```
POST http://localhost:8080/api/emails
Content-Type: application/json

{
  "email": "juan.gonzalez@gmail.com"
}
```

---

#### ms-Logs (8081)

**Registrar un log de error**
```
POST http://localhost:8081/api/logs
Content-Type: application/json

{
  "tipo_log": "ERROR",
  "servicio_origen": "ms-usuario",
  "mensaje_log": "No se pudo conectar a ms-direccion",
  "id_usuario": 1,
  "ip_log": "192.168.1.10",
  "detalle_log": "Connection refused: localhost:8084"
}
```

**Filtrar logs por tipo**
```
GET http://localhost:8081/api/logs/tipo/ERROR
```

**Obtener logs de un servicio**
```
GET http://localhost:8081/api/logs/servicio/ms-usuario
```

---

#### ms-Tutoriales (8082)

**Agregar una foto a un tutorial**
```
POST http://localhost:8082/api/fotos
Content-Type: application/json

{
  "foto": "https://ejemplo.com/imagen-tutorial.jpg",
  "id_tutor": 1
}
```

**Obtener galería de un tutorial**
```
GET http://localhost:8082/api/fotos/tutorial/1
```

---

#### ms-Progreso (8083)

**Registrar progreso inicial**
```
POST http://localhost:8083/api/progreso
Content-Type: application/json

{
  "id_usuario": 1,
  "id_tutorial": 1,
  "recursos_completados": 0,
  "cantidad_recursos_totales": 10,
  "preguntas_acertadas": 0,
  "preguntas_falladas": 0
}
```

**Actualizar progreso de un usuario**
```
PUT http://localhost:8083/api/progreso/1
Content-Type: application/json

{
  "id_usuario": 1,
  "id_tutorial": 1,
  "recursos_completados": 5,
  "cantidad_recursos_totales": 10,
  "preguntas_acertadas": 8,
  "preguntas_falladas": 2
}
```

**Ver progreso de un usuario en un tutorial específico**
```
GET http://localhost:8083/api/progreso/usuario/1/tutorial/1
```

---

#### ms-Dirección (8084)

El flujo correcto es de arriba hacia abajo: primero crear País, luego Región, luego Ciudad, luego Comuna, finalmente Dirección.

**Crear un país**
```
POST http://localhost:8084/api/paises
Content-Type: application/json

{
  "nombre_pais": "Chile"
}
```

**Crear una región**
```
POST http://localhost:8084/api/regiones
Content-Type: application/json

{
  "nombre_region": "Metropolitana",
  "id_pais": 1
}
```

**Crear una ciudad**
```
POST http://localhost:8084/api/ciudades
Content-Type: application/json

{
  "nombre_ciudad": "Santiago",
  "id_region": 1
}
```

**Crear una comuna**
```
POST http://localhost:8084/api/comunas
Content-Type: application/json

{
  "nombre_comuna": "Providencia",
  "id_ciudad": 1
}
```

**Crear una dirección**
```
POST http://localhost:8084/api/direcciones
Content-Type: application/json

{
  "calle": "Av. Providencia",
  "numero": "1234",
  "id_comuna": 1
}
```

**Obtener regiones de un país**
```
GET http://localhost:8084/api/regiones/pais/1
```

---

#### ms-Evaluaciones (8085)

**Crear una evaluación**
```
POST http://localhost:8085/api/evaluaciones
Content-Type: application/json

{
  "nombre_eva": "Evaluación de Python Básico",
  "tipo_eva": "QUIZ",
  "nivel_eva": "BASICO",
  "banco_preg": 20,
  "id_tutor": 1
}
```

**Obtener evaluaciones por nivel**
```
GET http://localhost:8085/api/evaluaciones/nivel/BASICO
```

**Obtener evaluaciones de un tutorial**
```
GET http://localhost:8085/api/evaluaciones/tutorial/1
```

---

#### ms-PreguntasRespuestas (8086)

El flujo de carga es jerárquico: primero crear el Cuestionario, luego sus Preguntas, luego las Alternativas de cada pregunta.

**Crear un cuestionario**
```
POST http://localhost:8086/api/cuestionarios
Content-Type: application/json

{
  "titulo_cuestionario": "Cuestionario de Python Básico",
  "descripcion_cuestionario": "Evalúa conocimientos de variables, tipos de datos y funciones",
  "id_tutor": 1
}
```

**Agregar una pregunta al cuestionario**
```
POST http://localhost:8086/api/preguntas
Content-Type: application/json

{
  "enunciado_pregunta": "¿Cuál es el tipo de dato para almacenar texto en Python?",
  "id_cuestionario": 1
}
```

**Agregar alternativas a la pregunta**
```
POST http://localhost:8086/api/alternativas
Content-Type: application/json

{
  "texto_alternativa": "str",
  "es_correcta": true,
  "id_pregunta": 1
}
```
```
POST http://localhost:8086/api/alternativas
Content-Type: application/json

{
  "texto_alternativa": "int",
  "es_correcta": false,
  "id_pregunta": 1
}
```

**Enviar respuestas y obtener corrección**
```
POST http://localhost:8086/api/cuestionarios/1/responder
Content-Type: application/json

{
  "id_usuario": 1,
  "respuestas": [
    { "id_pregunta": 1, "id_alternativa_seleccionada": 1 },
    { "id_pregunta": 2, "id_alternativa_seleccionada": 5 }
  ]
}
```

Respuesta esperada:
```json
{
  "id_resultado": 1,
  "correctas": 1,
  "incorrectas": 1,
  "porcentaje": 50.0
}
```

**Ver historial de un usuario en un cuestionario**
```
GET http://localhost:8086/api/resultados/usuario/1/cuestionario/1
```

**Obtener preguntas de un cuestionario (para el frontend)**
```
GET http://localhost:8086/api/preguntas/cuestionario/1
```

**Obtener alternativas de una pregunta (para el frontend)**
```
GET http://localhost:8086/api/alternativas/pregunta/1
```

---

## Consolas H2

Cada microservicio expone una consola web para inspeccionar su base de datos en memoria durante el desarrollo:

| Microservicio | URL Consola H2 | JDBC URL |
|---|---|---|
| ms-Usuario | http://localhost:8080/h2-console | `jdbc:h2:mem:usuariodb` |
| ms-Logs | http://localhost:8081/h2-console | `jdbc:h2:mem:logsdb` |
| ms-Tutoriales | http://localhost:8082/h2-console | `jdbc:h2:mem:tutorialesdb` |
| ms-Progreso | http://localhost:8083/h2-console | `jdbc:h2:mem:progresodb` |
| ms-Dirección | http://localhost:8084/h2-console | `jdbc:h2:mem:direcciondb` |
| ms-Evaluaciones | http://localhost:8085/h2-console | `jdbc:h2:mem:evaluacionesdb` |
| ms-PreguntasRespuestas | http://localhost:8086/h2-console | `jdbc:h2:mem:preguntasrespuestasdb` |

En la consola H2:
- **Driver Class:** `org.h2.Driver`
- **User Name:** `sa`
- **Password:** *(dejar vacío)*

> La base de datos H2 es en memoria (`create-drop`), por lo que se reinicia cada vez que el microservicio se detiene. Los datos no persisten entre reinicios.

---

## Resumen de puertos

| Microservicio | Puerto | Base de datos |
|---|---|---|
| ms-Usuario | 8080 | `usuariodb` |
| ms-Logs | 8081 | `logsdb` |
| ms-Tutoriales | 8082 | `tutorialesdb` |
| ms-Progreso | 8083 | `progresodb` |
| ms-Dirección | 8084 | `direcciondb` |
| ms-Evaluaciones | 8085 | `evaluacionesdb` |
| ms-PreguntasRespuestas | 8086 | `preguntasrespuestasdb` |

---

## Comunicación entre microservicios

```
ms-Usuario (8080)
    └──► ms-Dirección (8084)   [RestTemplate — valida que id_direccion exista]

ms-Evaluaciones (8085)        ──► ms-Tutoriales (8082)  [referencia por id_tutor, sin validación cruzada]
ms-Progreso (8083)            ──► ms-Usuario (8080)      [referencia por id_usuario, sin validación cruzada]
ms-Progreso (8083)            ──► ms-Tutoriales (8082)   [referencia por id_tutorial, sin validación cruzada]
ms-PreguntasRespuestas (8086) ──► ms-Tutoriales (8082)   [referencia por id_tutor en Cuestionario, sin validación cruzada]
ms-PreguntasRespuestas (8086) ──► ms-Progreso (8083)     [RestTemplate — actualiza preguntas_acertadas/falladas al corregir]
```
