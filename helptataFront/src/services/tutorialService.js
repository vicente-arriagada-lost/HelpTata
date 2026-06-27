// =============================================================
// SERVICIO DE TUTORIALES — tutorialService.js
// =============================================================
// Conecta con el microservicio ms-Tutoriales (puerto 8082).
//
// LECTURA:
//   getTutoriales()               → GET /api/tutoriales
//   getTutorialPorId(id)          → GET /api/tutoriales/{id}
//   getTutorialesPorCategoria(c)  → GET /api/tutoriales/categoria/{c}
//   getTutorialesPorNivel(n)      → GET /api/tutoriales/nivel/{n}
//   getTodasLasFotos()            → GET /api/fotos
//
// ADMIN (CRUD):
//   crearTutorial(data)           → POST   /api/tutoriales
//   actualizarTutorial(id, data)  → PUT    /api/tutoriales/{id}
//   eliminarTutorial(id)          → DELETE /api/tutoriales/{id}
// =============================================================
import { crearCliente } from './axiosConfig'

//* Cliente apuntando al microservicio de tutoriales
const api = crearCliente(import.meta.env.VITE_MS_TUTORIALES_URL || 'http://localhost:8082')

// ── LECTURA ───────────────────────────────────────────────────────────────────

/**
 * Obtiene la lista completa de tutoriales disponibles.
 * @returns {Promise<Array<{id_tutor, nombre_tuto, descripcion_tuto, tutorial, nivel_tuto, cat_tuto, tiempo_tutorial}>>}
 */
export const getTutoriales = () => api.get('/api/tutoriales')

/**
 * Obtiene un tutorial por su ID.
 * @param {number} id
 */
export const getTutorialPorId = (id) => api.get(`/api/tutoriales/${id}`)

/**
 * Filtra tutoriales por categoría.
 * @param {string} categoria
 */
export const getTutorialesPorCategoria = (categoria) =>
  api.get(`/api/tutoriales/categoria/${categoria}`)

/**
 * Filtra tutoriales por nivel de dificultad.
 * @param {'BASICO'|'INTERMEDIO'|'AVANZADO'} nivel
 */
export const getTutorialesPorNivel = (nivel) =>
  api.get(`/api/tutoriales/nivel/${nivel}`)

/**
 * Obtiene todas las fotos del sistema para mapearlas a sus tutoriales.
 * @returns {Promise<Array<{id_foto, foto, id_tutor}>>}
 */
export const getTodasLasFotos = () => api.get('/api/fotos')

// ── CONFIGURACIÓN ────────────────────────────────────────────────────────────

/**
 * Obtiene el valor de una configuración global por su clave.
 * Ejemplo: getConfiguracion('url_video_tutorial') → "http://servidor/media/tutorial.mp4"
 * @param {string} clave
 */
export const getConfiguracion = (clave) => api.get(`/api/config/${clave}`)

// ── ADMIN (CRUD) ──────────────────────────────────────────────────────────────

/**
 * Crea un nuevo tutorial (uso admin).
 * @param {{nombre_tuto, cat_tuto, nivel_tuto, tutorial, tiempo_tutorial, descripcion_tuto}} data
 */
export const crearTutorial = (data) => api.post('/api/tutoriales', data)

/**
 * Actualiza un tutorial existente (uso admin).
 * @param {number} id
 * @param {object} data
 */
export const actualizarTutorial = (id, data) => api.put(`/api/tutoriales/${id}`, data)

/**
 * Elimina un tutorial del sistema (uso admin).
 * @param {number} id
 */
export const eliminarTutorial = (id) => api.delete(`/api/tutoriales/${id}`)
