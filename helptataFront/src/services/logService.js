// =============================================================
// SERVICIO DE LOGS — logService.js
// =============================================================
// Registro y consulta de eventos del sistema.
// Apunta al microservicio ms-Logs (puerto 8081).
//
// LECTURA (uso admin):
//   getLogs()                   → GET /api/logs
//   getLogPorId(id)             → GET /api/logs/{id}
//   getLogsPorTipo(tipo)        → GET /api/logs/tipo/{tipo}
//   getLogsPorUsuario(idUser)   → GET /api/logs/usuario/{id}
//   getLogsPorServicio(nombre)  → GET /api/logs/servicio/{nombre}
//
// ESCRITURA:
//   crearLog(data)              → POST   /api/logs
//   actualizarLog(id, data)     → PUT    /api/logs/{id}
//   eliminarLog(id)             → DELETE /api/logs/{id}
//
// Valores válidos para tipo: "INFO" | "WARN" | "ERROR"
// Servicios típicos: "msUsuario", "msTutoriales", "msProgreso",
//   "msDireccion", "msEvaluaciones", "msPreguntasRespuestas"
// =============================================================
import { crearCliente } from './axiosConfig'

//* Cliente apuntando al microservicio de logs
const api = crearCliente(import.meta.env.VITE_MS_LOGS_URL || 'http://localhost:8081')

// ── LECTURA ───────────────────────────────────────────────────────────────────

/**
 * Obtiene todos los logs del sistema (uso admin/monitoreo).
 * @returns {Promise<Array<{id_log, tipo_log, servicio_origen, mensaje_log, fecha_hora_log, id_usuario, ip_log, detalle_log}>>}
 */
export const getLogs = () =>
  api.get('/api/logs')

/**
 * Obtiene un log por su ID.
 * @param {number} id
 * @returns {Promise<{id_log, tipo_log, servicio_origen, mensaje_log, fecha_hora_log, id_usuario, ip_log, detalle_log}>}
 */
export const getLogPorId = (id) =>
  api.get(`/api/logs/${id}`)

/**
 * Filtra logs por tipo de evento.
 * @param {'INFO'|'WARN'|'ERROR'} tipo
 * @returns {Promise<Array<{id_log, ...}>>}
 */
export const getLogsPorTipo = (tipo) =>
  api.get(`/api/logs/tipo/${tipo}`)

/**
 * Obtiene todos los logs de un usuario específico.
 * Útil para auditar la actividad de un usuario en el panel admin.
 * @param {number} idUsuario
 * @returns {Promise<Array<{id_log, ...}>>}
 */
export const getLogsPorUsuario = (idUsuario) =>
  api.get(`/api/logs/usuario/${idUsuario}`)

/**
 * Filtra logs por el microservicio que los generó.
 * @param {string} servicio - nombre del MS, ej: "msUsuario", "msTutoriales"
 * @returns {Promise<Array<{id_log, ...}>>}
 */
export const getLogsPorServicio = (servicio) =>
  api.get(`/api/logs/servicio/${servicio}`)

// ── ESCRITURA ─────────────────────────────────────────────────────────────────

/**
 * Registra un nuevo evento en el log del sistema.
 * La fecha_hora_log la asigna el backend automáticamente.
 * @param {{
 *   tipo_log: 'INFO'|'WARN'|'ERROR',
 *   servicio_origen: string,
 *   mensaje_log: string,
 *   id_usuario?: number,
 *   ip_log?: string,
 *   detalle_log?: string
 * }} data
 * @returns {Promise<{id_log, ...}>}
 */
export const crearLog = (data) =>
  api.post('/api/logs', data)

/**
 * Actualiza el contenido de un log existente.
 * Nota: la fecha_hora_log original no se modifica por diseño
 * (preserva el momento exacto en que ocurrió el evento).
 * @param {number} id
 * @param {{tipo_log?, servicio_origen?, mensaje_log?, id_usuario?, ip_log?, detalle_log?}} data
 * @returns {Promise<{id_log, ...}>}
 */
export const actualizarLog = (id, data) =>
  api.put(`/api/logs/${id}`, data)

/**
 * Elimina un log del sistema.
 * @param {number} id
 * @returns {Promise<string>}
 */
export const eliminarLog = (id) =>
  api.delete(`/api/logs/${id}`)
