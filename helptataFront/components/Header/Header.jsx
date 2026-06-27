// =============================================================
// COMPONENTE Header — Header.jsx
// =============================================================
// Barra de navegación principal de HelpTata. Muestra el logo,
// links de navegación y los botones de login/registro (o el
// botón de perfil si el usuario ya está autenticado).
//
// Props:
//   user         → objeto de usuario del AuthContext (o null)
//   onNavigate   → función para cambiar de página
//   showCourseButton → muestra el botón "Cursos" con scroll a la sección
//   showBackButton   → muestra "← Volver" en vez del logo clicable
//
// El botón "Admin" solo aparece cuando user.rol === 'ADMIN'.
// =============================================================
import { User, Menu, X, ShieldCheck } from 'lucide-react'
import { useState } from 'react'
import styles from './Header.module.scss'

export function Header({ user, onNavigate, showCourseButton = false, showBackButton = false }) {
  // Controla si el menú móvil (hamburguesa) está abierto o cerrado
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false)

  return (
    <header className={`${styles.header} text-white shadow-lg`}>
      {/* Enlace de salto de contenido para usuarios de teclado/lectores de pantalla */}
      <a
        href="#main-content"
        className={`${styles.skipLink} sr-only focus:not-sr-only focus:absolute focus:top-2 focus:left-2 focus:z-50 focus:bg-white focus:px-4 focus:py-2 focus:rounded-lg focus:font-semibold`}
      >
        Saltar al contenido principal
      </a>

      <div className="max-w-7xl mx-auto px-6 sm:px-8 py-5">
        <div className="flex items-center justify-between">
          {/* Logo / Botón de volver */}
          <div className="flex items-center">
            {showBackButton ? (
              <button
                onClick={() => onNavigate('main')}
                aria-label="Volver a la página principal"
                className={`${styles.btnBack} bg-white rounded-xl hover:bg-blue-50 transition-colors focus:outline-none focus:ring-4 focus:ring-white focus:ring-offset-2 focus:ring-offset-blue-900`}
              >
                ← Volver
              </button>
            ) : (
              <button
                onClick={() => onNavigate('main')}
                aria-label="HelpTata - Ir a la página principal"
                className={`${styles.logoBtn} text-white font-bold focus:outline-none focus:ring-4 focus:ring-white focus:ring-offset-2 focus:ring-offset-blue-900 rounded-lg px-1`}
              >
                HelpTata
              </button>
            )}
          </div>

          {/* Navegación de escritorio */}
          <nav aria-label="Navegación principal" className="hidden md:flex items-center gap-4">
            {/* Botón para ir a la sección de cursos con scroll suave */}
            {showCourseButton && (
              <button
                onClick={() => {
                  const cursosSection = document.getElementById('cursos')
                  if (cursosSection) cursosSection.scrollIntoView({ behavior: 'smooth' })
                }}
                className={`${styles.btnCourses} bg-white rounded-xl hover:bg-blue-50 transition-colors focus:outline-none focus:ring-4 focus:ring-white focus:ring-offset-2 focus:ring-offset-blue-900`}
              >
                Cursos
              </button>
            )}

            {/* Si hay usuario logueado: botón de perfil (+ Admin si corresponde). Si no: login + registro */}
            {user ? (
              <>
                {/* Botón Admin solo visible para usuarios con rol ADMIN */}
                {user.rol === 'ADMIN' && (
                  <button
                    onClick={() => onNavigate('admin')}
                    className={`${styles.btnAdmin} flex items-center justify-center gap-3 rounded-xl hover:opacity-90 transition-opacity focus:outline-none focus:ring-4 focus:ring-white focus:ring-offset-2 focus:ring-offset-blue-900`}
                  >
                    <ShieldCheck size={30} aria-hidden="true" />
                    <span>Admin</span>
                  </button>
                )}
                <button
                  onClick={() => onNavigate('profile')}
                  className={`${styles.btnProfile} flex items-center justify-center gap-3 bg-white rounded-xl hover:bg-blue-50 transition-colors focus:outline-none focus:ring-4 focus:ring-white focus:ring-offset-2 focus:ring-offset-blue-900`}
                >
                  <User size={30} aria-hidden="true" />
                  <span>Mi Perfil</span>
                </button>
              </>
            ) : (
              <>
                <button
                  onClick={() => onNavigate('register')}
                  className={`${styles.btnRegister} rounded-xl hover:opacity-90 transition-opacity focus:outline-none focus:ring-4 focus:ring-white focus:ring-offset-2 focus:ring-offset-blue-900`}
                >
                  Registrarse
                </button>
                <button
                  onClick={() => onNavigate('login')}
                  className={`${styles.btnLogin} bg-white rounded-xl hover:bg-blue-50 transition-colors focus:outline-none focus:ring-4 focus:ring-white focus:ring-offset-2 focus:ring-offset-blue-900`}
                >
                  Iniciar Sesión
                </button>
              </>
            )}
          </nav>

          {/* Botón hamburguesa para móvil */}
          <button
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            aria-expanded={mobileMenuOpen}
            aria-controls="mobile-menu"
            aria-label={mobileMenuOpen ? 'Cerrar menú de navegación' : 'Abrir menú de navegación'}
            className={`${styles.hamburger} md:hidden bg-white p-3 rounded-xl focus:outline-none focus:ring-4 focus:ring-white focus:ring-offset-2 focus:ring-offset-blue-900`}
          >
            {mobileMenuOpen
              ? <X size={32} aria-hidden="true" />
              : <Menu size={32} aria-hidden="true" />
            }
          </button>
        </div>

        {/* Menú móvil desplegable */}
        {mobileMenuOpen && (
          <nav id="mobile-menu" aria-label="Menú móvil" className="md:hidden mt-5 space-y-4 pb-4">
            {showCourseButton && (
              <button
                onClick={() => {
                  const cursosSection = document.getElementById('cursos')
                  if (cursosSection) cursosSection.scrollIntoView({ behavior: 'smooth' })
                  setMobileMenuOpen(false)
                }}
                className={`${styles.mobileBtnCourses} w-full bg-white rounded-xl hover:bg-blue-50 transition-colors focus:outline-none focus:ring-4 focus:ring-white focus:ring-offset-2 focus:ring-offset-blue-900`}
              >
                Cursos
              </button>
            )}

            {user ? (
              <>
                {user.rol === 'ADMIN' && (
                  <button
                    onClick={() => { onNavigate('admin'); setMobileMenuOpen(false) }}
                    className={`${styles.mobileBtnAdmin} w-full flex items-center justify-center gap-3 rounded-xl hover:opacity-90 transition-opacity focus:outline-none focus:ring-4 focus:ring-white focus:ring-offset-2 focus:ring-offset-blue-900`}
                  >
                    <ShieldCheck size={28} aria-hidden="true" />
                    <span>Panel Admin</span>
                  </button>
                )}
                <button
                  onClick={() => { onNavigate('profile'); setMobileMenuOpen(false) }}
                  className={`${styles.mobileBtnProfile} w-full flex items-center justify-center gap-3 bg-white rounded-xl hover:bg-blue-50 transition-colors focus:outline-none focus:ring-4 focus:ring-white focus:ring-offset-2 focus:ring-offset-blue-900`}
                >
                  <User size={28} aria-hidden="true" />
                  <span>Mi Perfil</span>
                </button>
              </>
            ) : (
              <>
                <button
                  onClick={() => { onNavigate('register'); setMobileMenuOpen(false) }}
                  className={`${styles.mobileBtnRegister} w-full rounded-xl hover:opacity-90 transition-opacity focus:outline-none focus:ring-4 focus:ring-white focus:ring-offset-2 focus:ring-offset-blue-900`}
                >
                  Registrarse
                </button>
                <button
                  onClick={() => { onNavigate('login'); setMobileMenuOpen(false) }}
                  className={`${styles.mobileBtnLogin} w-full bg-white rounded-xl hover:bg-blue-50 transition-colors focus:outline-none focus:ring-4 focus:ring-white focus:ring-offset-2 focus:ring-offset-blue-900`}
                >
                  Iniciar Sesión
                </button>
              </>
            )}
          </nav>
        )}
      </div>
    </header>
  )
}
