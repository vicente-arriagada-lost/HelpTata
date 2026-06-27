// =============================================================
// PÁGINA AdminUsuarios — AdminUsuarios.jsx
// =============================================================
// Muestra la lista completa de usuarios registrados en la
// plataforma. Permite buscar por nombre/email y eliminar
// usuarios (con confirmación). Usa react-toastify para
// notificaciones de éxito y error.
//
// Solo accesible con rol ADMIN (PrivateRoute en main.jsx).
// =============================================================
import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'react-toastify'
import { Users, Trash2, Search, X } from 'lucide-react'
import { getUsuarios, eliminarUsuario } from '../../services/usuarioService'
import styles from './AdminUsuarios.module.scss'

export default function AdminUsuarios() {
  const navigate = useNavigate()

  const [usuarios, setUsuarios] = useState([])
  const [loading, setLoading] = useState(true)
  const [busqueda, setBusqueda] = useState('')

  // ID del usuario que espera confirmación de borrado (null = ninguno)
  const [confirmDeleteId, setConfirmDeleteId] = useState(null)
  const [eliminando, setEliminando] = useState(false)

  useEffect(() => {
    async function cargar() {
      try {
        const res = await getUsuarios()
        setUsuarios(res.data)
      } catch {
        toast.error('No se pudieron cargar los usuarios.')
      } finally {
        setLoading(false)
      }
    }
    cargar()
  }, [])

  // Filtra la lista según la búsqueda por nombre o email
  const usuariosFiltrados = usuarios.filter((u) => {
    const texto = busqueda.toLowerCase()
    return (
      u.nombre_usuario?.toLowerCase().includes(texto) ||
      u.email_usuario?.toLowerCase().includes(texto)
    )
  })

  async function handleEliminar(id) {
    setEliminando(true)
    try {
      await eliminarUsuario(id)
      setUsuarios((prev) => prev.filter((u) => u.id_usuario !== id))
      toast.success('Usuario eliminado correctamente.')
    } catch {
      toast.error('No se pudo eliminar el usuario. Intenta nuevamente.')
    } finally {
      setEliminando(false)
      setConfirmDeleteId(null)
    }
  }

  const usuarioAEliminar = usuarios.find((u) => u.id_usuario === confirmDeleteId)

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
            <Users size={44} className={styles.headerIcon} aria-hidden="true" />
            <h1 className={`${styles.pageHeading} font-bold`}>
              Gestión de Usuarios
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
              placeholder="Buscar por nombre o email..."
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
              className={`${styles.searchInput} w-full rounded-xl focus:outline-none focus:ring-4 focus:ring-blue-300`}
              aria-label="Buscar usuarios por nombre o email"
            />
          </div>

          {/* Estado de carga */}
          {loading && (
            <p aria-live="polite" className={styles.loadingText}>Cargando usuarios...</p>
          )}

          {/* Tabla de usuarios */}
          {!loading && (
            <>
              <p className={`${styles.countText} mb-4`}>
                {usuariosFiltrados.length} usuario{usuariosFiltrados.length !== 1 ? 's' : ''} encontrado{usuariosFiltrados.length !== 1 ? 's' : ''}
              </p>
              <div className={`${styles.tableWrapper} overflow-x-auto rounded-xl`}>
                <table className="w-full" aria-label="Tabla de usuarios">
                  <thead>
                    <tr className={styles.tableHead}>
                      <th className={`${styles.th} text-left py-4 px-5 font-bold`}>ID</th>
                      <th className={`${styles.th} text-left py-4 px-5 font-bold`}>Nombre</th>
                      <th className={`${styles.th} text-left py-4 px-5 font-bold`}>Email</th>
                      <th className={`${styles.th} text-left py-4 px-5 font-bold`}>Rol</th>
                      <th className={`${styles.th} text-center py-4 px-5 font-bold`}>Acción</th>
                    </tr>
                  </thead>
                  <tbody>
                    {usuariosFiltrados.length === 0 ? (
                      <tr>
                        <td colSpan={5} className={`${styles.emptyCell} text-center py-8`}>
                          No se encontraron usuarios.
                        </td>
                      </tr>
                    ) : (
                      usuariosFiltrados.map((u, idx) => (
                        <tr
                          key={u.id_usuario}
                          style={{ backgroundColor: idx % 2 === 0 ? '#ffffff' : '#f9fafb', borderTop: '1px solid #e5e7eb' }}
                        >
                          <td className={`${styles.tdText} py-4 px-5`}>{u.id_usuario}</td>
                          <td className={`${styles.tdText} py-4 px-5`}>{u.nombre_usuario}</td>
                          <td className={`${styles.tdText} py-4 px-5`}>{u.email_usuario}</td>
                          <td className="py-4 px-5">
                            {/* Badge color is dynamic based on rol — kept inline */}
                            <span
                              className={`${styles.badge} rounded-full px-3 py-1 font-bold`}
                              style={{
                                backgroundColor: u.tipo_rol === 'ADMIN' ? '#fef3c7' : '#eaf0f8',
                                color: u.tipo_rol === 'ADMIN' ? '#b45309' : '#1e3a5f',
                              }}
                            >
                              {u.tipo_rol ?? 'USER'}
                            </span>
                          </td>
                          <td className="py-4 px-5 text-center">
                            <button
                              onClick={() => setConfirmDeleteId(u.id_usuario)}
                              aria-label={`Eliminar usuario ${u.nombre_usuario}`}
                              className={`${styles.deleteBtn} rounded-xl hover:opacity-80 transition-opacity focus:outline-none focus:ring-4 focus:ring-red-300`}
                            >
                              <Trash2 size={20} aria-hidden="true" />
                            </button>
                          </td>
                        </tr>
                      ))
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
              ¿Estás seguro que deseas eliminar a <strong>{usuarioAEliminar?.nombre_usuario}</strong>?
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
