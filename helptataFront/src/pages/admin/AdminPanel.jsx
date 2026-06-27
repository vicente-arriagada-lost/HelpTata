// =============================================================
// PÁGINA AdminPanel — AdminPanel.jsx
// =============================================================
// Panel de control del administrador. Muestra tarjetas con
// accesos rápidos a las secciones de gestión: usuarios y
// tutoriales. Solo accesible con rol ADMIN (protegido por
// PrivateRoute en main.jsx).
//
// Usa react-router-dom directamente (useNavigate) porque es
// un componente nuevo, no uno legacy con prop onNavigate.
// =============================================================
import { useNavigate } from 'react-router-dom'
import { Users, BookOpen, ShieldCheck } from 'lucide-react'
import { useAuth } from '../../hooks/useAuth'
import styles from './AdminPanel.module.scss'

export default function AdminPanel() {
  const navigate = useNavigate()
  const { usuario } = useAuth()

  const secciones = [
    {
      titulo: 'Gestión de Usuarios',
      descripcion: 'Ver, buscar y eliminar usuarios del sistema.',
      icono: <Users size={48} aria-hidden="true" className={styles.sectionIcon} />,
      ruta: '/admin/usuarios',
    },
    {
      titulo: 'Gestión de Tutoriales',
      descripcion: 'Ver y eliminar tutoriales disponibles en la plataforma.',
      icono: <BookOpen size={48} aria-hidden="true" className={styles.sectionIcon} />,
      ruta: '/admin/tutoriales',
    },
  ]

  return (
    <div className={`${styles.pageWrapper} min-h-screen py-10 sm:py-14 px-4 sm:px-6`}>
      <main id="main-content" className="max-w-4xl mx-auto">
        {/* Botón de volver al inicio */}
        <button
          onClick={() => navigate('/')}
          className={`${styles.backBtn} mb-8 hover:bg-blue-50 transition-colors focus:outline-none focus:ring-4 focus:ring-white focus:ring-offset-2 focus:ring-offset-blue-900`}
        >
          ← Volver al inicio
        </button>

        <div className="bg-white rounded-2xl shadow-2xl p-8 sm:p-14">
          {/* Cabecera */}
          <div className="text-center mb-10 sm:mb-14">
            <div
              className={`${styles.avatarBox} inline-flex items-center justify-center rounded-full mb-6`}
              aria-hidden="true"
            >
              <ShieldCheck size={56} className={styles.avatarIcon} />
            </div>
            <h1 className={`${styles.panelHeading} font-bold mb-3`}>
              Panel de Administración
            </h1>
            <p className={styles.welcomeText}>
              Bienvenido, <strong>{usuario?.nombre}</strong>. Selecciona una sección para gestionar.
            </p>
          </div>

          {/* Tarjetas de secciones */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
            {secciones.map((seccion) => (
              <button
                key={seccion.ruta}
                onClick={() => navigate(seccion.ruta)}
                className={`${styles.sectionCard} rounded-2xl p-8 text-left hover:shadow-lg transition-all focus:outline-none focus:ring-4 focus:ring-blue-400`}
              >
                <div className="mb-4">{seccion.icono}</div>
                <h2 className={`${styles.sectionTitle} font-bold mb-2`}>
                  {seccion.titulo}
                </h2>
                <p className={styles.sectionDesc}>
                  {seccion.descripcion}
                </p>
              </button>
            ))}
          </div>
        </div>
      </main>
    </div>
  )
}
