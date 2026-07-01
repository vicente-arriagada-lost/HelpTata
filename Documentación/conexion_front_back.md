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

## 8. Cómo llega internet hasta el servidor

### El problema: IP dinámica

El servidor de HelpTata no está en un datacenter con IP fija — está en una red local con una **IP pública dinámica** (cambia cada cierto tiempo). Eso crea dos problemas:
1. ¿Cómo saber siempre cuál es la IP actual?
2. ¿Cómo evitar abrir puertos en el router (que muchas veces no se puede)?

Ambos se resuelven con DDNS + Cloudflare Tunnel.

---

### DDNS — mantener la IP actualizada

Un **script que corre en el servidor** consulta periódicamente cuál es su IP pública actual y la reporta al proveedor DDNS. Así, aunque la IP cambie, el nombre de dominio siempre sabe a dónde apuntar.

```
Servidor (IP cambia)
      │
      │  script DDNS: "mi IP ahora es 200.90.169.141"
      ▼
Proveedor DDNS
      │
      │  actualiza el registro
      ▼
200.90.169.141  ← siempre apunta a la IP actual del servidor
```

---

### Cloudflare Tunnel — exponer los servicios sin abrir puertos

En lugar de abrir puertos en el router (que requiere acceso al router y expone el servidor directamente a internet), se usa **Cloudflare Tunnel**.

El servidor establece una conexión **saliente** hacia Cloudflare. Cloudflare recibe las peticiones de los usuarios y las reenvía por ese túnel hasta el servidor. El router no necesita configuración.

```
Usuario en internet
      │
      │  https://helptata.cl
      ▼
Cloudflare (maneja HTTPS, certificados, protección)
      │
      │  túnel cifrado (conexión saliente del servidor)
      ▼
cloudflared (servicio en el servidor)
      │
      ▼
Docker containers (localhost)
```

### Qué subdominio apunta a qué puerto

El archivo `~/.cloudflared/config.yml` del servidor define el mapeo completo:

| Subdominio | Puerto local | Servicio |
|---|---|---|
| `helptata.cl` | `:5000` | Frontend (Nginx + React) |
| `www.helptata.cl` | `:5000` | Frontend |
| `ms-usuario.helptata.cl` | `:8080` | ms-Usuario |
| `ms-logs.helptata.cl` | `:8081` | ms-Logs |
| `ms-tutoriales.helptata.cl` | `:8082` | ms-Tutoriales |
| `ms-progreso.helptata.cl` | `:8083` | ms-Progreso |
| `ms-direccion.helptata.cl` | `:8084` | ms-Dirección |
| `ms-evaluaciones.helptata.cl` | `:8085` | ms-Evaluaciones |
| `ms-preguntas.helptata.cl` | `:8086` | ms-Preguntas |
| `ms-tatabot.helptata.cl` | `:8087` | ms-TataBot |
| `videos.helptata.cl` | `:9000` | Servidor de videos |
| `imagenes.helptata.cl` | `:9001` | Servidor de imágenes |

### cloudflared como servicio del sistema

`cloudflared` corre como servicio de systemd — arranca automáticamente cuando el servidor enciende y se reinicia solo si falla:

```bash
sudo systemctl enable cloudflared   # arranca al encender
sudo systemctl start cloudflared    # iniciar ahora
sudo systemctl status cloudflared   # verificar que está corriendo
```

### Flujo completo de una petición real

```
1. Usuario abre helptata.cl en el navegador
2. DNS de Cloudflare resuelve helptata.cl → red de Cloudflare
3. Cloudflare recibe la petición HTTPS (certifica el dominio)
4. Lo reenvía por el túnel cifrado al servidor
5. cloudflared en el servidor lo pasa a localhost:5000
6. Nginx del frontend lo sirve (archivos React o proxy a un MS)
7. Si es /api/usuarios → Nginx hace proxy a ms-usuario:8080
8. ms-usuario responde → sube por el mismo camino hasta el usuario
```

---

## 9. Cómo funciona el servidor en producción

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

## 9. Cómo se almacenan y reproducen los videos

### Dónde viven los archivos

Los videos **no están en el repositorio** — son archivos `.mp4` guardados en una carpeta del servidor. La ruta de esa carpeta se define en el `.env`:

```env
VIDEOS_PATH=/ruta/en/el/servidor/donde/estan/los/videos
```

Docker monta esa carpeta como volumen de solo lectura dentro del contenedor `videos-server`.

### Quién sirve los videos

Un contenedor Nginx dedicado (`videos-server`) los sirve en el puerto `9000`:

```
Archivo en disco del servidor
  /ruta/videos/Internet Seguro.mp4
        │
        │ (montado como volumen)
        ▼
  Contenedor videos-server (Nginx :9000)
        │
        │ GET http://helptata.cl:9000/Internet%20Seguro.mp4
        ▼
  Plyr (reproductor de video en el browser)
```

La configuración clave de este Nginx (`docker/nginx/media.conf`) hace dos cosas esenciales:

1. **CORS abierto** — permite que el browser cargue el video aunque venga de un puerto distinto (`:9000` vs `:443`):
```nginx
add_header Access-Control-Allow-Origin  *  always;
add_header Access-Control-Allow-Headers 'Range, Content-Type' always;
add_header Access-Control-Expose-Headers 'Content-Range, Accept-Ranges, Content-Length' always;
```

2. **Range requests** — Nginx los soporta nativamente. Esto es lo que permite que el usuario pueda mover la barra de progreso del video (buscar en el tiempo) sin tener que descargar todo el archivo primero.

### Dónde se guarda la URL del video en la base de datos

Las URLs no están hardcodeadas en el código — se guardan en la BD y el frontend las pide a la API:

**Videos de los cursos** → tabla `tutorial` en `helptata_tutoriales`:
```
tutorial.tutorial = "http://IP_SERVIDOR:9000/Internet Seguro.mp4"
```
(el campo se llama `tutorial` aunque guarda la URL — es un nombre heredado del diseño original)

**Video introductorio de la homepage** → tabla `configuracion` en `helptata_tutoriales`:
```
clave_config = "url_video_tutorial"
valor_config = "http://IP_SERVIDOR:9000/VideoIntroductorio.mp4"
```

### Cómo llega la URL al reproductor

```
1. Frontend pide GET /api/tutoriales
   └─► ms-tutoriales devuelve lista de cursos con campo { tutorial: "http://...9000/video.mp4" }

2. MainPage llama GET /api/config/url_video_tutorial
   └─► ms-tutoriales devuelve la URL del video introductorio

3. El frontend pone la URL en el <video>:
   <source src={course.videoUrl} type="video/mp4" />

4. Plyr toma el <video> y añade sus controles encima
   └─► el browser descarga el video en chunks via Range requests desde :9000
```

### Para agregar o cambiar un video

1. Copiar el archivo `.mp4` a la carpeta del servidor definida en `VIDEOS_PATH`
2. Actualizar la URL en la base de datos (tabla `tutorial` o `configuracion`)
3. No hace falta reiniciar ningún contenedor — Nginx sirve los archivos directamente del disco

---

## 11. Cómo se conecta la app Android con la página web

La app no replica la web — la **envuelve**. Tiene su propio registro/login nativo y luego carga `helptata.cl` dentro de un WebView.

### Arquitectura general

```
App Android
    │
    ├─ Pantallas nativas (Kotlin + Jetpack Compose)
    │   └─ Registro (7 pasos) y Login  ──► Retrofit ──► ms-usuario.helptata.cl
    │                                           │
    │                                    recibe JWT
    │                                           │
    └─ WebAppScreen (WebView)  ◄────────────────┘
            │
            │  carga: helptata.cl/auth-callback?token=eyJ...
            ▼
       helptata.cl (la misma web que el navegador)
```

### Paso a paso del login en la app

```
1. Usuario escribe email + contraseña en la pantalla nativa de login
        │
        ▼
2. Retrofit llama POST https://ms-usuario.helptata.cl/api/usuarios/login
        │
        ▼
3. ms-usuario valida, genera JWT y lo devuelve
        │
        ▼
4. La app guarda el token en EncryptedSharedPreferences (cifrado en el dispositivo)
   TokenStore.save(ctx, token)
        │
        ▼
5. La app abre WebAppScreen y carga la URL:
   https://helptata.cl/auth-callback?token=eyJhbGci...
        │
        ▼
6. AuthCallback.jsx (en la web) lee el token del query param,
   llama a login(token), lo guarda en localStorage y redirige a /
        │
        ▼
7. La web funciona exactamente igual que en el navegador —
   el WebView es un navegador integrado dentro de la app
```

### Cómo la app recuerda la sesión

Al abrir la app, `TokenStore.load()` intenta leer el token guardado:

```
App abre
    │
    ├─ Hay token guardado ──► carga WebAppScreen directamente (sin pedir login)
    │
    └─ No hay token ──────► muestra pantalla de login
```

Si el token está corrupto o es incompatible (por ejemplo, al reinstalar la app con una firma diferente), `TokenStore` borra las credenciales automáticamente y pide login de nuevo.

### Comunicación entre la web y la app nativa

Dentro del WebView, la web puede llamar funciones nativas de Android usando un **JavaScript Bridge**:

```
JavaScript (helptata.cl)          Android nativo
         │                              │
         │  Android.logout()  ──────►  AndroidBridge.logout()
         │                              │
         │                         borra el token
         │                         muestra pantalla de login
```

Esto permite que el botón "Cerrar sesión" de la web también cierre la sesión en la app nativa.

### Pantalla completa del video en la app

Cuando el usuario activa pantalla completa en el reproductor de video (Plyr), el WebView lo detecta y lo maneja de forma nativa:

```
Usuario toca botón fullscreen en Plyr
        │
        ▼
WebChromeClient.onShowCustomView() se activa
        │
        ├─ Fuerza orientación landscape
        ├─ Agrega la vista del video sobre toda la pantalla
        └─ Activa modo inmersivo (oculta barra de sistema)
               → el usuario desliza desde el borde para que aparezca

Al salir de fullscreen (botón de Plyr o botón Atrás del teléfono):
        │
        ▼
onHideCustomView() restaura la orientación y la barra del sistema
```

---

## Resumen en una línea

> El usuario hace login → el backend genera un JWT firmado → el frontend lo guarda en localStorage → lo envía en cada petición como `Authorization: Bearer` → el backend lo verifica con el mismo secreto → si es válido, responde con los datos.
