# ms-TataBot — Puerto 8087

Microservicio de inteligencia artificial que potencia al chatbot TataBot de HelpTata. Recibe el mensaje del usuario junto con el historial de conversación, lo envía a la API de Groq y devuelve una respuesta adaptada a adultos mayores.

---

## Tecnología

| Herramienta | Versión | Para qué se usa |
|---|---|---|
| Spring Boot | 3.5.15 | Framework principal |
| Java | 21 | Lenguaje |
| Spring Security | 6.x | Seguridad y CORS |
| Spring Web | 6.x | API REST + RestTemplate |
| Lombok | — | Reducción de boilerplate |
| Groq API | — | Motor de IA (modelo `openai/gpt-oss-120b`) |

**Sin base de datos** — stateless. El historial de conversación se mantiene en el frontend y se envía con cada request.

---

## Requisitos previos

- Java 21
- Maven 3.8+
- API key de Groq (obtener gratis en https://console.groq.com)

---

## Configuración

Editar `src/main/resources/application.properties`:

```properties
spring.application.name=ms-tatabot
server.port=8087

groq.api.key=TU_API_KEY_DE_GROQ
groq.model=openai/gpt-oss-120b
groq.url=https://api.groq.com/openai/v1/chat/completions
```

> No subir el archivo al repositorio con la API key real.

---

## Ejecución

```bash
cd msTataBot
mvn spring-boot:run
```

El MS queda disponible en `http://localhost:8087`.

---

## Endpoint

### `POST /api/tatabot/chat`

Envía un mensaje del usuario y recibe la respuesta generada por la IA.

**Request:**
```json
{
  "mensaje": "¿Cómo entro a mi correo electrónico?",
  "historial": [
    { "role": "user", "content": "hola" },
    { "role": "assistant", "content": "¡Hola! ¿En qué puedo ayudarte hoy?" }
  ]
}
```

| Campo | Tipo | Descripción |
|---|---|---|
| `mensaje` | String | Mensaje actual del usuario |
| `historial` | Array | Mensajes previos de la conversación (puede ser vacío `[]`) |
| `historial[].role` | String | `"user"` o `"assistant"` |
| `historial[].content` | String | Texto del mensaje |

**Response:**
```json
{
  "respuesta": "Vamos paso a paso para que puedas entrar a tu correo.\n\nPaso 1\nAbre el programa para navegar..."
}
```

**Probar con curl:**
```bash
curl -X POST http://localhost:8087/api/tatabot/chat \
  -H "Content-Type: application/json" \
  -d '{"mensaje":"hola","historial":[]}'
```

---

## Integración con ms-Logs

Este servicio reporta errores a `ms-Logs` (puerto 8081) de forma fire-and-forget. Requiere que `ms.logs.url=http://localhost:8081` esté en `application.properties`.

El `GlobalExceptionHandler` captura todas las excepciones no manejadas (ej. fallo de la API de Groq, timeout) y las registra como `ERROR`. Las `ResponseStatusException` 4xx se registran como `WARNING`.

---

## Seguridad

- CSRF desactivado (API REST stateless)
- CORS habilitado para `http://localhost:5173`, `http://localhost:5000` y `http://helptata.cl:5000`
- Todos los endpoints son públicos (`permitAll`) — no requiere JWT
- El control de acceso se delega al sistema prompt de la IA

---

## Sistema prompt

El comportamiento de la IA está controlado por un system prompt definido en `GroqService.java` que incluye:

- **Scope permitido:** tecnología, cursos de HelpTata, seguridad digital, dispositivos
- **Estilo de respuesta:** lenguaje simple, pasos numerados, sin tecnicismos, comparaciones cotidianas
- **Formato obligatorio:** mensaje introductorio + Paso 1, Paso 2... (sin markdown)
- **Prohibiciones absolutas — seguridad:** hacking, SQL injection, XSS, malware, ingeniería social, evasión de seguridad
- **Prohibición de código:** PROHIBIDO generar código en cualquier lenguaje (HTML, CSS, JS, Python, Java, SQL, Markdown, etc.), sin importar el tamaño del fragmento ni el pretexto del usuario
- **Prohibición de archivos:** PROHIBIDO generar o describir el contenido de archivos de ningún tipo (documentos, scripts, plantillas, hojas de cálculo, etc.)
- **Respuesta ante código/archivos:** "Lo siento, no puedo escribir código ni generar archivos. Soy TataBot y estoy aquí para ayudarte con preguntas sobre tecnología y los cursos de HelpTata. ¿Tienes alguna duda sobre cómo usar la tecnología?"
- **Detección de gibberish:** si el usuario escribe caracteres sin sentido, responde con mensaje de error amable

---

## Despliegue en producción

En producción este microservicio corre en un contenedor Docker. La API key de Groq se pasa como variable de entorno para no exponerla en el código.

```bash
docker compose up --build ms-tatabot -d
```

La variable `GROQ_API_KEY` se define en el `.env` del proyecto raíz y es inyectada automáticamente por `docker-compose.yml`.

---

## Estructura del proyecto

```
msTataBot/
├── src/
│   └── main/
│       ├── java/com/TataBot/ms/
│       │   ├── MsApplication.java          # Punto de entrada
│       │   ├── clients/
│       │   │   └── LogClient.java          # Reporta errores a ms-Logs (fire-and-forget)
│       │   ├── config/
│       │   │   ├── GlobalExceptionHandler.java  # Captura todas las excepciones → ms-Logs
│       │   │   └── SecurityConfig.java     # CORS + Spring Security
│       │   ├── controller/
│       │   │   └── TataBotController.java  # POST /api/tatabot/chat
│       │   ├── dto/
│       │   │   ├── ChatRequest.java        # { mensaje, historial[] }
│       │   │   └── ChatResponse.java       # { respuesta }
│       │   └── service/
│       │       └── GroqService.java        # Lógica de llamada a Groq API
│       └── resources/
│           └── application.properties
└── pom.xml
```
