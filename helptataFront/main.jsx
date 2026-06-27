// =============================================================
// COMPONENTE RAÍZ App — main.jsx
// =============================================================
// Punto de entrada de la aplicación. Gestiona la navegación
// con react-router-dom (rutas reales en la URL) y delega la
// autenticación al AuthContext.
//
// Rutas disponibles:
//   /              → Página principal (MainPage)
//   /login         → Formulario de inicio de sesión
//   /registro      → Formulario de registro
//   /perfil        → Perfil del usuario (requiere auth)
//   /curso         → Detalle de un curso (requiere auth, course en state)
//   /tutorial      → Tutorial de cómo responder el cuestionario
//   /quiz          → Cuestionario (requiere auth, course en state)
//   /admin         → Panel de administración (requiere rol ADMIN)
//   /admin/usuarios    → Gestión de usuarios (requiere rol ADMIN)
//   /admin/tutoriales  → Gestión de tutoriales (requiere rol ADMIN)
//   *              → Redirige a /
//
// Patrón de wrappers:
//   Los componentes existentes usan el prop onNavigate(page) con
//   strings ('main', 'login', etc.). Los wrappers traducen esas
//   llamadas a navigate(path) de react-router sin tocar los
//   componentes originales.
// =============================================================
import { useEffect, useState } from 'react'
import { BrowserRouter, Routes, Route, useNavigate, useLocation, Navigate } from 'react-router-dom'
import { ToastContainer } from 'react-toastify'
import 'react-toastify/dist/ReactToastify.css'
import { AuthProvider } from './src/context/AuthContext'
import { useAuth } from './src/hooks/useAuth'
import PrivateRoute from './src/components/PrivateRoute/PrivateRoute'
import { MainPage } from './components/MainPage/MainPage'
import { Login } from './components/Login/Login'
import { Register } from './components/Register/Register'
import { UserProfile } from './components/UserProfile/UserProfile'
import { CoursePage } from './components/CoursePage/CoursePage'
import { TutorialQuiz } from './components/TutorialQuiz/TutorialQuiz'
import { Quiz } from './components/Quiz/Quiz'
import { getProgresoPorUsuarioYTutorial } from './src/services/progresoService'
import AdminPanel from './src/pages/admin/AdminPanel'
import AdminUsuarios from './src/pages/admin/AdminUsuarios'
import AdminTutoriales from './src/pages/admin/AdminTutoriales'
import AuthCallback from './src/pages/AuthCallback'

function ScrollToTop() {
  const { pathname } = useLocation()
  useEffect(() => {
    const t = setTimeout(() => {
      window.scrollTo({ top: 0, left: 0, behavior: 'instant' })
    }, 0)
    return () => clearTimeout(t)
  }, [pathname])
  return null
}

// Mapa de strings usados por onNavigate() → rutas reales de la app
const PAGE_PATHS = {
  main: '/',
  login: '/login',
  register: '/registro',
  profile: '/perfil',
  course: '/curso',
  tutorial: '/tutorial',
  quiz: '/quiz',
  admin: '/admin',
  'admin-usuarios': '/admin/usuarios',
  'admin-tutoriales': '/admin/tutoriales',
}

// Hook que convierte onNavigate('page') al navigate('/path') de react-router
function useNavigateAdapter() {
  const navigate = useNavigate()
  return (page, state) => navigate(PAGE_PATHS[page] ?? '/', { state })
}

// ── Wrappers de rutas ─────────────────────────────────────────────────────────
// Cada wrapper adapta el API de props (onNavigate, onLogin, etc.) al
// sistema de rutas de react-router sin modificar los componentes originales.

function MainPageWrapper() {
  const { usuario } = useAuth()
  const onNavigate = useNavigateAdapter()
  const navigate = useNavigate()
  return (
    <MainPage
      user={usuario}
      onNavigate={onNavigate}
      onSelectCourse={(course) => navigate('/curso', { state: { course } })}
    />
  )
}

function LoginWrapper() {
  const onNavigate = useNavigateAdapter()
  const navigate = useNavigate()
  return (
    <Login
      onLogin={() => navigate('/')}
      onNavigate={onNavigate}
    />
  )
}

function RegisterWrapper() {
  const onNavigate = useNavigateAdapter()
  const navigate = useNavigate()
  return (
    <Register
      onRegister={() => navigate('/login')}
      onNavigate={onNavigate}
    />
  )
}

function UserProfileWrapper() {
  const { usuario, logout } = useAuth()
  const onNavigate = useNavigateAdapter()
  const navigate = useNavigate()
  return (
    <UserProfile
      user={usuario}
      onLogout={() => { logout(); navigate('/login') }}
      onNavigate={onNavigate}
    />
  )
}

function CoursePageWrapper() {
  const { usuario } = useAuth()
  const { state } = useLocation()
  const navigate = useNavigate()
  const onNavigate = useNavigateAdapter()
  const course = state?.course
  const [progresoActual, setProgresoActual] = useState(0)

  useEffect(() => {
    if (!usuario?.id || !course?.id) return
    getProgresoPorUsuarioYTutorial(usuario.id, course.id)
      .then(res => setProgresoActual(Math.round(res.data.porcentaje_progreso ?? 0)))
      .catch(() => setProgresoActual(0))
  }, [usuario?.id, course?.id])

  //* Si se navega directo a /curso sin un curso en el state → volver al inicio
  if (!course) return <Navigate to="/" replace />

  return (
    <CoursePage
      course={course}
      user={usuario}
      progress={progresoActual}
      onNavigate={(page) => {
        if (page === 'quiz') navigate('/quiz', { state: { course } })
        else if (page === 'tutorial') navigate('/tutorial', { state: { course } })
        else onNavigate(page)
      }}
    />
  )
}

function TutorialQuizWrapper() {
  const { state } = useLocation()
  const navigate = useNavigate()
  const onNavigate = useNavigateAdapter()
  const course = state?.course
  return (
    <TutorialQuiz
      onNavigate={(page) => {
        if (page === 'quiz' && course) navigate('/quiz', { state: { course } })
        else onNavigate(page)
      }}
    />
  )
}

function QuizWrapper() {
  const { usuario } = useAuth()
  const { state } = useLocation()
  const navigate = useNavigate()
  const onNavigate = useNavigateAdapter()
  const course = state?.course

  //* Si se navega directo a /quiz sin un curso → volver al inicio
  if (!course) return <Navigate to="/" replace />

  return (
    <Quiz
      course={course}
      user={usuario}
      onComplete={() => navigate('/curso', { state: { course } })}
      onNavigate={onNavigate}
    />
  )
}

// ── Árbol de rutas ────────────────────────────────────────────────────────────

function AppRoutes() {
  return (
    <>
      {/* Notificaciones globales de react-toastify */}
      <ToastContainer position="top-right" autoClose={3000} />

      <ScrollToTop />
      <Routes>
        {/* Ruta principal — requiere autenticación */}
        <Route path="/" element={
          <PrivateRoute>
            <MainPageWrapper />
          </PrivateRoute>
        } />
        <Route path="/login" element={<LoginWrapper />} />
        <Route path="/registro" element={<RegisterWrapper />} />
        <Route path="/tutorial" element={<TutorialQuizWrapper />} />
        <Route path="/auth-callback" element={<AuthCallback />} />

        {/* Rutas que requieren solo estar autenticado */}
        <Route path="/perfil" element={
          <PrivateRoute>
            <UserProfileWrapper />
          </PrivateRoute>
        } />
        <Route path="/curso" element={
          <PrivateRoute>
            <CoursePageWrapper />
          </PrivateRoute>
        } />
        <Route path="/quiz" element={
          <PrivateRoute>
            <QuizWrapper />
          </PrivateRoute>
        } />

        {/* Rutas exclusivas de administrador */}
        <Route path="/admin" element={
          <PrivateRoute roles={['ADMIN']}>
            <AdminPanel />
          </PrivateRoute>
        } />
        <Route path="/admin/usuarios" element={
          <PrivateRoute roles={['ADMIN']}>
            <AdminUsuarios />
          </PrivateRoute>
        } />
        <Route path="/admin/tutoriales" element={
          <PrivateRoute roles={['ADMIN']}>
            <AdminTutoriales />
          </PrivateRoute>
        } />

        {/* Cualquier ruta desconocida → inicio */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </>
  )
}

// BrowserRouter envuelve toda la app para habilitar el enrutamiento.
// AuthProvider va dentro del Router para que PrivateRoute pueda usar useNavigate.
export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </BrowserRouter>
  )
}
