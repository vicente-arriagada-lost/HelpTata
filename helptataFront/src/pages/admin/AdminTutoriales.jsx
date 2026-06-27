// =============================================================
// PÁGINA AdminTutoriales — AdminTutoriales.jsx
// =============================================================
// Muestra la lista completa de tutoriales. Permite buscar
// por nombre/categoría/nivel y eliminar tutoriales (con
// confirmación). Usa react-toastify para notificaciones.
//
// Solo accesible con rol ADMIN (PrivateRoute en main.jsx).
// =============================================================
import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'react-toastify'
import { BookOpen, Trash2, Search, X } from 'lucide-react'
import { getTutoriales, eliminarTutorial } from '../../services/tutorialService'
import styles from './AdminTutoriales.module.scss'

// Colores para el badge de nivel de dificultad — dynamic, kept inline
const NIVEL_ESTILOS = {
  BASICO:      { bg: '#dcfce7', color: '#166534' },
  INTERMEDIO:  { bg: '#fef9c3', color: '#854d0e' },
  AVANZADO:    { bg: '#fee2e2', color: '#991b1b' },
}

export default function AdminTutoriales() {
  const navigate = useNavigate()

  const [tutoriales, setTutoriales] = useState([])
  const [loading, setLoading] = useState(true)
  const [busqueda, setBusqueda] = useState('')

  const [confirmDeleteId, setConfirmDeleteId] = useState(null)
  const [eliminando, setEliminando] = useState(false)

  useEffect(() => {
    async function cargar() {
      try {
        const res = await getTutoriales()
        setTutoriales(res.data)
      } catch {
        toast.error('No se pudieron cargar los tutoriales.')
      } finally {
        setLoading(false)
      }
    }
    cargar()
  }, [])

  const tutorialesFiltrados = tutoriales.filter((t) => {
    const texto = busqueda.toLowerCase()
    return (
      t.nombre_tuto?.toLowerCase().includes(texto) ||
      t.cat_tuto?.toLowerCase().includes(texto) ||
      t.nivel_tuto?.toLowerCase().includes(texto)
    )
  })

  async function handleEliminar(id) {
    setEliminando(true)
    try {
      await eliminarTutorial(id)
      setTutoriales((prev) => prev.filter((t) => t.id_tutor !== id))
      toast.success('Tutorial eliminado correctamente.')
    } catch {
      toast.error('No se pudo eliminar el tutorial. Intenta nuevamente.')
    } finally {
      setEliminando(false)
      setConfirmDeleteId(null)
    }
  }

  const tutorialAEliminar = tutoriales.find((t) => t.id_tutor === confirmDeleteId)

  return (
    <div className={`${styles.pageWrapper} min-h-screen py-10 sm:py-14 px-4 sm:px-6`}>
      <main id="main-content" className="max-w-5xl mx-auto">
        {/* Botón volver al panel */}
        <button
          onClick={() => navigate('/admin')}
          className={`${styles.backBtn} mb-8 hover:bg-blue-50 transition-colors focus:outline-none focus:ring-4 focus:ring-white focus:ring-offset-2 focus:ring-offset-blue-900`}
        >
          ← Volver al panel
        </button>

        <div className="bg-white rounded-2xl shadow-2xl p-8 sm:p-12">
          {/* Cabecera */}
          <div className="flex items-center gap-5 mb-8">
            <BookOpen size={44} className={styles.headerIcon} aria-hidden="true" />
            <h1 className={`${styles.pageHeading} font-bold`}>
              Gestión de Tutoriales
            </h1>
          </div>

          {/* Buscador */}
          <div className="relative mb-8">
            <Search
              size={22}
              aria-hidden="true"
              className={`${styles.searchIcon} absolute left-4 top-1/2 -translate-y-1/2`}
            />
            <input
              type="search"
              placeholder="Buscar por nombre, categoría o nivel..."
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
              className={`${styles.searchInput} w-full rounded-xl focus:outline-none focus:ring-4 focus:ring-blue-300`}
              aria-label="Buscar tutoriales"
            />
          </div>

          {loading && (
            <p aria-live="polite" className={styles.loadingText}>Cargando tutoriales...</p>
          )}

          {!loading && (
            <>
              <p className={`${styles.countText} mb-4`}>
                {tutorialesFiltrados.length} tutorial{tutorialesFiltrados.length !== 1 ? 'es' : ''} encontrado{tutorialesFiltrados.length !== 1 ? 's' : ''}
              </p>
              <div className={`${styles.tableWrapper} overflow-x-auto rounded-xl`}>
                <table className="w-full" aria-label="Tabla de tutoriales">
                  <thead>
                    <tr className={styles.tableHead}>
                      <th className={`${styles.th} text-left py-4 px-5 font-bold`}>ID</th>
                      <th className={`${styles.th} text-left py-4 px-5 font-bold`}>Nombre</th>
                      <th className={`${styles.th} text-left py-4 px-5 font-bold`}>Categoría</th>
                      <th className={`${styles.th} text-left py-4 px-5 font-bold`}>Nivel</th>
                      <th className={`${styles.th} text-left py-4 px-5 font-bold`}>Tiempo</th>
                      <th className={`${styles.th} text-center py-4 px-5 font-bold`}>Acción</th>
                    </tr>
                  </thead>
                  <tbody>
                    {tutorialesFiltrados.length === 0 ? (
                      <tr>
                        <td colSpan={6} className={`${styles.emptyCell} text-center py-8`}>
                          No se encontraron tutoriales.
                        </td>
                      </tr>
                    ) : (
                      tutorialesFiltrados.map((t, idx) => {
                        const nivelEstilo = NIVEL_ESTILOS[t.nivel_tuto] ?? { bg: '#f3f4f6', color: '#374151' }
                        return (
                          <tr
                            key={t.id_tutor}
                            style={{ backgroundColor: idx % 2 === 0 ? '#ffffff' : '#f9fafb', borderTop: '1px solid #e5e7eb' }}
                          >
                            <td className={`${styles.tdText} py-4 px-5`}>{t.id_tutor}</td>
                            <td className={`${styles.tdBold} py-4 px-5`}>{t.nombre_tuto}</td>
                            <td className={`${styles.tdText} py-4 px-5`}>{t.cat_tuto}</td>
                            <td className="py-4 px-5">
                              {/* Badge color is dynamic per nivel — kept inline */}
                              <span
                                className={`${styles.badge} rounded-full px-3 py-1 font-bold`}
                                style={{ backgroundColor: nivelEstilo.bg, color: nivelEstilo.color }}
                              >
                                {t.nivel_tuto}
                              </span>
                            </td>
                            <td className={`${styles.tdText} py-4 px-5`}>{t.tiempo_tutorial}</td>
                            <td className="py-4 px-5 text-center">
                              <button
                                onClick={() => setConfirmDeleteId(t.id_tutor)}
                                aria-label={`Eliminar tutorial ${t.nombre_tuto}`}
                                className={`${styles.deleteBtn} rounded-xl hover:opacity-80 transition-opacity focus:outline-none focus:ring-4 focus:ring-red-300`}
                              >
                                <Trash2 size={20} aria-hidden="true" />
                              </button>
                            </td>
                          </tr>
                        )
                      })
                    )}
                  </tbody>
                </table>
              </div>
            </>
          )}
        </div>
      </main>

      {/* Modal de confirmación de eliminación */}
      {confirmDeleteId && (
        <div
          role="dialog"
          aria-modal="true"
          aria-labelledby="confirm-delete-title"
          className={`${styles.modalOverlay} fixed inset-0 flex items-center justify-center px-4`}
        >
          <div className="bg-white rounded-2xl shadow-2xl p-8 sm:p-10 max-w-md w-full">
            <div className="flex items-center justify-between mb-6">
              <h2 id="confirm-delete-title" className={`${styles.modalTitle} font-bold`}>
                Confirmar eliminación
              </h2>
              <button
                onClick={() => setConfirmDeleteId(null)}
                aria-label="Cancelar"
                className={`${styles.modalCloseBtn} rounded-lg focus:outline-none focus:ring-4 focus:ring-gray-300`}
              >
                <X size={28} className={styles.modalCloseIcon} />
              </button>
            </div>
            <p className={styles.modalText}>
              ¿Estás seguro que deseas eliminar el tutorial <strong>{tutorialAEliminar?.nombre_tuto}</strong>?
              Esta acción no se puede deshacer.
            </p>
            <div className="flex gap-4">
              <button
                onClick={() => handleEliminar(confirmDeleteId)}
                disabled={eliminando}
                className={`${styles.modalDeleteBtn} flex-1 rounded-xl hover:opacity-90 transition-opacity focus:outline-none focus:ring-4 focus:ring-red-300`}
                style={{ cursor: eliminando ? 'not-allowed' : 'pointer' }}
              >
                {eliminando ? 'Eliminando...' : 'Sí, eliminar'}
              </button>
              <button
                onClick={() => setConfirmDeleteId(null)}
                autoFocus
                className={`${styles.modalCancelBtn} flex-1 rounded-xl transition-colors focus:outline-none focus:ring-4 focus:ring-gray-300`}
              >
                Cancelar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
