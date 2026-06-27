# HelpTata — Frontend

Interfaz web de la plataforma HelpTata, diseñada para adultos mayores. Permite explorar cursos de alfabetización digital, ver videos tutoriales, responder cuestionarios y hacer seguimiento del progreso personal. Incluye un panel de administración para gestionar usuarios y tutoriales.

---

## Tecnologías

| Herramienta | Versión | Para qué se usa |
|---|---|---|
| React | 18 | Librería de interfaces |
| Vite | 5 | Empaquetador y servidor de desarrollo |
| Tailwind CSS | 3 | Estilos utilitarios |
| Axios | 1.7 | Peticiones HTTP al backend |
| react-router-dom | 6.22 | Navegación con URLs reales (BrowserRouter) |
| react-toastify | 10 | Notificaciones de éxito y error |
| Lucide React | 0.344 | Iconos |
| Plyr | 3 | Reproductor de video con controles tipo YouTube |
| Vitest | 1.6 | Framework de pruebas unitarias |

---

## Requisitos previos

- Node.js 18 o superior
- npm 9 o superior
- Los 8 microservicios del backend corriendo (ver README general del proyecto)

---

## Instalación

```bash
cd helptataFront
npm install
```

---

## Configuración

El archivo `.env` en la raíz del proyecto contiene las URLs de cada microservicio. Si no existe, crearlo con el siguiente contenido:

```env
VITE_MS_USUARIO_URL=http://localhost:8080
VITE_MS_LOGS_URL=http://localhost:8081
VITE_MS_TUTORIALES_URL=http://localhost:8082
VITE_MS_PROGRESO_URL=http://localhost:8083
VITE_MS_DIRECCION_URL=http://localhost:8084
VITE_MS_EVALUACIONES_URL=http://localhost:8085
VITE_MS_PREGUNTAS_URL=http://localhost:8086
VITE_MS_TATABOT_URL=http://localhost:8087
```

> No subir este archivo al repositorio si contiene URLs de producción.

---

## Ejecución

```bash
npm run dev
```

La aplicación queda disponible en `http://localhost:5000`.

---

## Estructura del proyecto

```
helptataFront/
├── components/                   # Componentes legacy (usan prop onNavigate)
│   ├── CourseCard/               # Tarjeta de vista previa de un curso
│   ├── CoursePage/               # Página de detalle de un curso
│   ├── Footer/                   # Pie de página
│   ├── Header/                   # Cabecera con navegación y botón Admin (ADMIN)
│   ├── Login/                    # Formulario de inicio de sesión
│   ├── MainPage/                 # Página principal con lista de cursos
│   ├── ProgressCircle/           # Círculo visual de progreso
│   ├── Quiz/                     # Cuestionario interactivo
│   ├── Register/                 # Formulario de registro
│   ├── TataBot/                  # Chatbot IA conectado a ms-TataBot (puerto 8087)
│   ├── TutorialQuiz/             # Tutorial de uso del cuestionario
│   └── UserProfile/              # Perfil, progreso y edición de datos del usuario
├── src/
│   ├── components/
│   │   └── PrivateRoute/
│   │       └── PrivateRoute.jsx  # Guard de rutas: requiere auth + rol opcional
│   ├── context/
│   │   └── AuthContext.jsx       # Estado global: token, usuario, hasRole()
│   ├── hooks/
│   │   └── useAuth.js            # Hook para consumir el AuthContext
│   ├── pages/
│   │   └── admin/
│   │       ├── AdminPanel.jsx    # Panel admin: accesos a secciones
│   │       ├── AdminUsuarios.jsx # Tabla de usuarios con búsqueda y eliminación
│   │       └── AdminTutoriales.jsx # Tabla de tutoriales con búsqueda y eliminación
│   ├── services/
│   │   ├── axiosConfig.js        # Fábrica de clientes axios con interceptor JWT
│   │   ├── authService.js        # Login y registro contra ms-Usuario
│   │   ├── tutorialService.js    # CRUD tutoriales y fotos (ms-Tutoriales)
│   │   ├── progresoService.js    # Progreso del usuario (ms-Progreso)
│   │   ├── cuestionarioService.js # Preguntas y respuestas (ms-Preguntas)
│   │   ├── usuarioService.js     # CRUD usuarios, roles y emails (ms-Usuario)
│   │   ├── ubicacionService.js   # Países, regiones, ciudades, comunas (ms-Dirección)
│   │   ├── evaluacionService.js  # CRUD evaluaciones (ms-Evaluaciones)
│   │   ├── logService.js         # CRUD logs del sistema (ms-Logs)
│   │   └── tataBotService.js     # Envío de mensajes al chatbot IA (ms-TataBot)
│   └── validators/
│       ├── fieldValidators.js    # Validación de email (máx 100), contraseña (máx 20), teléfono, nombre
│       └── rutValidators.js      # Validación de RUT chileno (módulo 11)
├── main.jsx                      # Raíz de la app: BrowserRouter + rutas + wrappers
├── .env                          # Variables de entorno (no subir al repositorio)
└── package.json
```

---

## Comunicación con el backend

Todos los servicios usan `axios` con una fábrica común (`crearCliente`) que agrega automáticamente:

- **Cabecera `Authorization: Bearer <token>`** en cada petición (cuando el usuario está autenticado).
- **Redirección al login** si el backend responde con `401 Unauthorized`.

Cada microservicio tiene su propio cliente independiente porque corren en puertos distintos:

| Servicio | Variable de entorno | Puerto |
|---|---|---|
| ms-Usuario | `VITE_MS_USUARIO_URL` | 8080 |
| ms-Logs | `VITE_MS_LOGS_URL` | 8081 |
| ms-Tutoriales | `VITE_MS_TUTORIALES_URL` | 8082 |
| ms-Progreso | `VITE_MS_PROGRESO_URL` | 8083 |
| ms-Dirección | `VITE_MS_DIRECCION_URL` | 8084 |
| ms-Evaluaciones | `VITE_MS_EVALUACIONES_URL` | 8085 |
| ms-PreguntasRespuestas | `VITE_MS_PREGUNTAS_URL` | 8086 |
| ms-TataBot | `VITE_MS_TATABOT_URL` | 8087 |

---

## Autenticación y autorización

- El estado de sesión se gestiona con `AuthContext` y se persiste en `localStorage`.
- Claves de almacenamiento: `helptata_token` (JWT) y `helptata_user` (objeto con `id`, `nombre`, `email`, `rol`).
- El JWT se adjunta automáticamente en cada petición via el interceptor de `axiosConfig.js`.
- `isAuthenticated` requiere que tanto el token como el objeto de usuario estén presentes simultáneamente.
- `hasRole(rol)` verifica el rol del usuario; acepta string (`'ADMIN'`) o array (`['ADMIN', 'USER']`).
- Los roles almacenados son `"ADMIN"` y `"USER"` (sin prefijo `ROLE_`).

### PrivateRoute

El componente `PrivateRoute` protege rutas del router:

```jsx
// Solo requiere estar autenticado
<PrivateRoute>
  <UserProfile />
</PrivateRoute>

// Requiere rol ADMIN; otros roles son redirigidos a /
<PrivateRoute roles={['ADMIN']}>
  <AdminPanel />
</PrivateRoute>
```

Si el usuario no está autenticado, es redirigido a `/login` y tras autenticarse vuelve automáticamente a la URL que intentó acceder.

---

## Navegación (react-router-dom)

La app usa `BrowserRouter` con URLs reales. Los componentes legacy usan el prop `onNavigate(page)` con strings; los wrappers en `main.jsx` traducen esas llamadas a `navigate(path)` sin modificar los componentes originales.

### Rutas disponibles

| Ruta | Componente | Protección |
|---|---|---|
| `/` | MainPage | Requiere auth (redirige a `/login` si no autenticado) |
| `/login` | Login | Pública |
| `/registro` | Register | Pública |
| `/tutorial` | TutorialQuiz | Pública |
| `/auth-callback` | — | Recibe token JWT desde la app Android vía query param |
| `/perfil` | UserProfile | Requiere auth |
| `/curso` | CoursePage | Requiere auth + course en state |
| `/quiz` | Quiz | Requiere auth + course en state |
| `/admin` | AdminPanel | Requiere rol ADMIN |
| `/admin/usuarios` | AdminUsuarios | Requiere rol ADMIN |
| `/admin/tutoriales` | AdminTutoriales | Requiere rol ADMIN |
| `*` | Redirige a `/login` | — |

> Las rutas `/curso` y `/quiz` reciben el objeto `course` via `location.state` (pasado con `navigate('/curso', { state: { course } })`).

---

## Panel de administración

Accesible desde el botón **Admin** del Header (visible solo para usuarios con rol `ADMIN`), o navegando directamente a `/admin`.

| Sección | Ruta | Funcionalidades |
|---|---|---|
| Panel principal | `/admin` | Accesos rápidos a usuarios y tutoriales |
| Gestión de usuarios | `/admin/usuarios` | Ver lista, buscar por nombre/email, eliminar con confirmación |
| Gestión de tutoriales | `/admin/tutoriales` | Ver lista, buscar por nombre/categoría/nivel, eliminar con confirmación |

Todas las acciones de eliminación muestran un modal de confirmación y una notificación (react-toastify) con el resultado.

---

## Despliegue en producción

En producción el frontend se construye como una imagen Docker usando un build multi-etapa y se sirve con Nginx en el puerto `5000`.

```bash
# Desde la raíz del proyecto HelpTata
docker compose up --build frontend -d
```

La IP/dominio del servidor se pasa como build arg `SERVER_IP` desde el `.env` del proyecto raíz. Vite la usa al construir el bundle para configurar las URLs de los microservicios.

```bash
# Contenido mínimo del .env en la raíz del proyecto
SERVER_IP=helptata.cl
```

> No crear un `.env` local dentro de `helptataFront/` en producción — el Dockerfile lo genera automáticamente durante el build.

---

## Pruebas unitarias

Las pruebas están ubicadas en `src/validators/` junto a los archivos que prueban (sufijo `.test.js`).

**Ejecutar todas las pruebas (una sola vez):**
```bash
npm test
```

**Modo observación (re-ejecuta al guardar archivos):**
```bash
npm run test:watch
```

**Interfaz visual en el navegador:**
```bash
npm run test:ui
```

### Archivos de test

| Archivo | Qué prueba |
|---|---|
| `src/validators/rutValidators.test.js` | `formatRut`, `validarRutChileno`, `esMayorDe18`, `MAX_FECHA` |
| `src/validators/fieldValidators.test.js` | `validarEmail`, `validarPassword`, `validarConfirmPassword`, `validarTelefono`, `validarNombre`, `noSoloEspacios`, `passwordRules` |

### Cobertura de casos

Los tests cubren:
- Valores válidos que deben pasar sin error
- Valores vacíos o `null`
- Formatos incorrectos
- Casos borde (exactamente 18 años, contraseña con exactamente `MIN_PASS` caracteres, etc.)
- Cálculo del dígito verificador del RUT (módulo 11)

---

## Reproductor de video

Los videos de tutoriales y el video introductorio usan **Plyr** en lugar del elemento `<video controls>` nativo. Plyr proporciona una barra de controles consistente similar a YouTube con: play/pausa, barra de progreso, tiempo actual, volumen, velocidad y pantalla completa.

**Color principal:** `#2ecc71` (verde HelpTata), configurado con la variable CSS `--plyr-color-main`.

**Controles habilitados:** `play-large`, `play`, `progress`, `current-time`, `mute`, `volume`, `settings` (velocidad), `fullscreen`.

> Nota técnica: `<React.StrictMode>` está desactivado en `src/main.jsx` porque Plyr no es compatible con el ciclo doble de montaje que StrictMode aplica en desarrollo. Esto no afecta las builds de producción.

---

## Validadores de formularios

Los límites de los campos se definen como constantes exportadas en `src/validators/fieldValidators.js`:

| Constante | Valor | Se aplica en |
|---|---|---|
| `MAX_EMAIL` | 100 caracteres | Login, Register, UserProfile |
| `MIN_PASS` | 8 caracteres | Login, Register, UserProfile |
| `MAX_PASS` | 20 caracteres | Login, Register, UserProfile |
| `MAX_NOMBRE` | 40 caracteres | Register |

La validación de teléfono (`validarTelefono`) se aplica únicamente en el editor de perfil (`UserProfile`) y en el formulario de registro (`Register`). El login solo valida email y contraseña.
