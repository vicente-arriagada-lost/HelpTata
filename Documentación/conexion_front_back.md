# Cómo se conecta el Frontend con el Backend en HelpTata

---

## 1. Visión general

```
[Usuario]
   │
   ├─ Web (helptata.cl) ──► React (frontend) ──► API REST (ms-Usuario :8080, ms-Tutoriales :8082, etc.)
   │
   └─ App Android ──────────► Login nativo ──────► ms-Usuario :8080
                               (Retrofit)              │
                                                       └─► JWT ──► WebView carga helptata.cl/auth-callback?token=...
```

El frontend **nunca accede directo a la base de datos**. Todo pasa por HTTP a los microservicios.

---

## 2. Cómo funciona el JWT (JSON Web Token)

Un JWT es un string con tres partes separadas por puntos:

```
header.payload.firma
```

- **header**: algoritmo usado (HS256)
- **payload**: datos del usuario (no cifrados, solo firmados)
- **firma**: garantiza que nadie alteró el payload

### Qué guarda el payload en HelpTata

```json
{
  "sub": "usuario@email.com",
  "id":  42,
  "nombre": "María",
  "rut": "12345678-9",
  "rol": "USER",
  "iat": 1719700000,
  "exp": 1719786400
}
```

`iat` = cuándo se creó · `exp` = cuándo vence (24h después)

> El payload lo puede leer cualquiera — **nunca guardes contraseñas ahí**.
> La firma solo la puede generar el servidor, que tiene el secreto (`app.jwt.secret`).

---

## 3. Flujo de login (web)

```
Frontend                          ms-Usuario (:8080)
   │                                    │
   │── POST /api/usuarios/login ────────►│
   │   { email, password }              │
   │                                    │── BCrypt verifica contraseña en BD
   │                                    │── Genera JWT firmado con HMAC-SHA256
   │◄─ { token: "eyJ..." } ────────────│
   │                                    │
   │  localStorage.setItem('helptata_token', token)
   │  (el frontend decodifica el payload para leer id, nombre, rol)
```

### Cómo el frontend envía el token en cada petición

```js
// Todos los servicios incluyen el token en el header:
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

Esto lo hace `axiosConfig.js` automáticamente con un interceptor — no hay que ponerlo a mano en cada llamada.

---

## 4. Cómo el backend valida cada petición

Cada microservicio tiene un **JwtFilter** que se ejecuta antes de cualquier endpoint:

```
Petición HTTP llega
      │
      ▼
JwtFilter.doFilterInternal()
      │
      ├─ ¿Tiene header "Authorization: Bearer ..."?
      │     NO ──► pasa sin autenticar (Spring decide si rechaza con 401)
      │
      ├─ Extrae el token y lee el email del payload
      │
      ├─ Carga el usuario desde la BD para confirmar que existe
      │
      ├─ Verifica firma + que no haya vencido
      │     MAL ──► pasa sin autenticar → 401
      │
      └─ OK ──► registra al usuario como autenticado en Spring Security
                (el endpoint puede ejecutarse normalmente)
```

---

## 5. Flujo de login en la App Android

La app Android tiene su propio login nativo (Retrofit llama a `ms-usuario.helptata.cl`).
Una vez que recibe el JWT, lo pasa al WebView de esta forma:

```
App Android                        WebView (helptata.cl)
     │                                    │
     │── Abre URL: /auth-callback?token=eyJ...
     │                                    │
     │                          AuthCallback.jsx lee el token
     │                          llama a login(token)
     │                          guarda en localStorage
     │                          redirige a /
     │                                    │
     │                          (la web funciona igual que si el usuario
     │                           hubiera hecho login desde el navegador)
```

El token se guarda en `EncryptedSharedPreferences` en el dispositivo para que la app no pida login cada vez que se abre.

---

## 6. Cuándo expira el token

- El token dura **24 horas** (configurado en `app.jwt.expiration`)
- El frontend tiene un timer que cierra la sesión exactamente cuando vence
- Al volver a la pestaña después de un tiempo, también re-verifica si sigue válido
- Si el token vence, el usuario ve la pantalla de login automáticamente

---

## 7. CORS — por qué el browser permite las peticiones

El frontend en `helptata.cl` llama a `api.helptata.cl` (dominio distinto).
Por defecto el browser bloquearía esto. Cada microservicio tiene un `WebConfig.java` que permite explícitamente el origen:

```java
registry.addMapping("/**")
        .allowedOrigins("https://helptata.cl", "http://localhost:5173")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true);
```

---

## 8. Cómo funciona el servidor en producción

### Todo corre en Docker

El servidor VPS corre un solo comando que levanta todo:

```bash
docker compose up -d --build
```

Esto arranca **13 contenedores** al mismo tiempo:

```
┌─────────────────────────────────────────────────────────┐
│  VPS (helptata.cl)                                      │
│                                                         │
│  [Internet] ──► Caddy (:443 HTTPS) ──► frontend (:5000)│
│                                               │         │
│                    ┌──────────────────────────┘         │
│                    ▼                                     │
│             Nginx (dentro del contenedor frontend)       │
│                    │                                     │
│      ┌─────────────┼──────────────────────┐             │
│      ▼             ▼                      ▼             │
│  ms-usuario    ms-tutoriales    ms-preguntas ...        │
│   (:8080)        (:8082)           (:8086)              │
│      │             │                  │                  │
│      └─────────────┴──────────────────┘                 │
│                    ▼                                     │
│              PostgreSQL (:5432)                          │
│           (7 bases de datos separadas)                   │
└─────────────────────────────────────────────────────────┘
```

### El rol clave del Nginx del frontend

El contenedor `frontend` no solo sirve los archivos React — también actúa como **reverse proxy** para todos los microservicios. Así el browser siempre habla con un solo dominio (`helptata.cl`) y no hay problemas de CORS en producción:

```
Browser pide: GET helptata.cl/api/tutoriales/1
      │
      ▼
Nginx (frontend container)
      │
      └──► proxy_pass http://ms-tutoriales:8082/api/tutoriales/1
                (red interna de Docker, no expuesta a internet)
```

El mapeo completo de rutas está en `helptataFront/nginx.conf`:

| URL que ve el browser | Microservicio destino |
|---|---|
| `/api/usuarios` | ms-usuario :8080 |
| `/api/tutoriales` | ms-tutoriales :8082 |
| `/api/progreso` | ms-progreso :8083 |
| `/api/direcciones` | ms-direccion :8084 |
| `/api/cuestionarios` | ms-preguntas :8086 |
| `/api/tatabot` | ms-tatabot :8087 |
| `/swagger-ui/` | ms-usuario :8080 |
| `/*` (todo lo demás) | `index.html` (SPA) |

### Variables de entorno del servidor

El archivo `.env` en el servidor guarda los datos sensibles que Docker inyecta a cada contenedor al arrancar:

```env
DB_PASSWORD=...        # contraseña de PostgreSQL
JWT_SECRET=...         # secreto para firmar los tokens JWT
SERVER_IP=...          # IP del servidor (usada al compilar el frontend)
GROQ_API_KEY=...       # clave de la API de Groq para TataBot
VIDEOS_PATH=...        # carpeta con los videos en el servidor
IMAGENES_PATH=...      # carpeta con las imágenes en el servidor
```

Ninguno de estos valores está en el repositorio. Si el `.env` no existe, `docker compose up` falla.

### Cómo se actualiza el servidor

```bash
# En el servidor, dentro de /root/HelpTata (o donde esté el proyecto):
git pull
docker compose up -d --build frontend   # solo reconstruir lo que cambió
```

Cada microservicio tiene su propio `Dockerfile` con build en dos etapas:
1. Maven compila el `.jar`
2. Solo el `.jar` se copia a una imagen mínima `eclipse-temurin:21-jre-alpine`

Esto hace que las imágenes sean pequeñas y el deploy rápido.

---

## Resumen en una línea

> El usuario hace login → el backend genera un JWT firmado → el frontend lo guarda en localStorage → lo envía en cada petición como `Authorization: Bearer` → el backend lo verifica con el mismo secreto → si es válido, responde con los datos.
