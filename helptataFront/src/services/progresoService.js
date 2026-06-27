// =============================================================
// SERVICIO DE PROGRESO — progresoService.js
// =============================================================
// Conecta con el microservicio ms-Progreso (puerto 8083)
// para leer y actualizar el avance de cada usuario en sus
// tutoriales.
//
// getProgresoUsuario(idUsuario):
//   GET /api/progreso/usuario/{id} → historial completo del usuario
//   Incluye: porcentaje_progreso, recursos_completados, etc.
//
// crearProgreso(data):
//   POST /api/progreso → inicia el registro de progreso para
//   un usuario en un tutorial (se llama la primera vez).
//
// actualizarProgreso(id, data):
//   PUT /api/progreso/{id} → actualiza los valores de un registro
//   existente (ej: tras completar el cuestionario).
// =============================================================
import { crearCliente } from './axiosConfig'

// Cliente apuntando al microservicio de progreso
const api = crearCliente(import.meta.env.VITE_MS_PROGRESO_URL || 'http://localhost:8083')

/**
 * Obtiene el historial de progreso de un usuario en todos sus tutoriales.
 * @param {number} idUsuario
 * @returns {Promise<Array<{id_progreso, id_usuario, id_tutorial, porcentaje_progreso, recursos_completados, cantidad_recursos_totales, preguntas_acertadas, preguntas_falladas}>>}
 */
export const getProgresoUsuario = (idUsuario) =>
  api.get(`/api/progreso/usuario/${idUsuario}`)

/**
 * Obtiene el progreso de un usuario en un tutorial específico.
 * Lanza 404 si no existe aún registro para ese par usuario+tutorial.
 */
export const getProgresoPorUsuarioYTutorial = (idUsuario, idTutorial) =>
  api.get(`/api/progreso/usuario/${idUsuario}/tutorial/${idTutorial}`)

/**
 * Crea un registro de progreso inicial para un usuario en un tutorial.
 * @param {{id_usuario, id_tutorial, recursos_completados, cantidad_recursos_totales, preguntas_acertadas, preguntas_falladas}} data
 * @returns {Promise<object>}
 */
export const crearProgreso = (data) => api.post('/api/progreso', data)

/**
 * Actualiza un registro de progreso existente.
 * @param {number} id - id_progreso a actualizar
 * @param {object} data - Nuevos valores del progreso
 * @returns {Promise<object>}
 */
export const actualizarProgreso = (id, data) => api.put(`/api/progreso/${id}`, data)
