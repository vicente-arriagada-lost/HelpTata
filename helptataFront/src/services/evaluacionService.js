// =============================================================
// SERVICIO DE EVALUACIONES — evaluacionService.js
// =============================================================
// Gestión del catálogo de evaluaciones de la plataforma.
// Apunta al microservicio ms-Evaluaciones (puerto 8085).
//
// LECTURA:
//   getEvaluaciones()              → GET /api/evaluaciones
//   getEvaluacionPorId(id)         → GET /api/evaluaciones/{id}
//   getEvaluacionesPorTutorial(id) → GET /api/evaluaciones/tutorial/{id}
//   getEvaluacionesPorNivel(nivel) → GET /api/evaluaciones/nivel/{nivel}
//   getEvaluacionesPorTipo(tipo)   → GET /api/evaluaciones/tipo/{tipo}
//
// ADMIN (CRUD):
//   crearEvaluacion(data)          → POST   /api/evaluaciones
//   actualizarEvaluacion(id, data) → PUT    /api/evaluaciones/{id}
//   eliminarEvaluacion(id)         → DELETE /api/evaluaciones/{id}
//
// Valores válidos para nivel: "BASICO" | "INTERMEDIO" | "AVANZADO"
// Valores válidos para tipo:  "QUIZ" | "EXAMEN"
// =============================================================
import { crearCliente } from './axiosConfig'

//* Cliente apuntando al microservicio de evaluaciones
const api = crearCliente(import.meta.env.VITE_MS_EVALUACIONES_URL || 'http://localhost:8085')

// ── LECTURA ───────────────────────────────────────────────────────────────────

/**
 * Obtiene todas las evaluaciones del sistema.
 * @returns {Promise<Array<{id_eva, nombre_eva, tipo_eva, nivel_eva, banco_preg, id_tutor}>>}
 */
export const getEvaluaciones = () =>
  api.get('/api/evaluaciones')

/**
 * Obtiene una evaluación por su ID.
 * @param {number} id
 * @returns {Promise<{id_eva, nombre_eva, tipo_eva, nivel_eva, banco_preg, id_tutor}>}
 */
export const getEvaluacionPorId = (id) =>
  api.get(`/api/evaluaciones/${id}`)

/**
 * Obtiene las evaluaciones de un tutorial específico.
 * Úsalo para mostrar los tests disponibles dentro de un curso.
 * @param {number} idTutor
 * @returns {Promise<Array<{id_eva, nombre_eva, tipo_eva, nivel_eva, banco_preg, id_tutor}>>}
 */
export const getEvaluacionesPorTutorial = (idTutor) =>
  api.get(`/api/evaluaciones/tutorial/${idTutor}`)

/**
 * Filtra evaluaciones por nivel de dificultad.
 * @param {'BASICO'|'INTERMEDIO'|'AVANZADO'} nivel
 * @returns {Promise<Array<{id_eva, ...}>>}
 */
export const getEvaluacionesPorNivel = (nivel) =>
  api.get(`/api/evaluaciones/nivel/${nivel}`)

/**
 * Filtra evaluaciones por tipo (quiz o examen).
 * @param {'QUIZ'|'EXAMEN'} tipo
 * @returns {Promise<Array<{id_eva, ...}>>}
 */
export const getEvaluacionesPorTipo = (tipo) =>
  api.get(`/api/evaluaciones/tipo/${tipo}`)

// ── ADMIN (CRUD) ──────────────────────────────────────────────────────────────

/**
 * Crea una nueva evaluación.
 * @param {{
 *   nombre_eva: string,
 *   tipo_eva: 'QUIZ'|'EXAMEN',
 *   nivel_eva: 'BASICO'|'INTERMEDIO'|'AVANZADO',
 *   banco_preg: number,
 *   id_tutor: number
 * }} data
 * @returns {Promise<{id_eva, nombre_eva, tipo_eva, nivel_eva, banco_preg, id_tutor}>}
 */
export const crearEvaluacion = (data) =>
  api.post('/api/evaluaciones', data)

/**
 * Actualiza una evaluación existente.
 * @param {number} id
 * @param {{nombre_eva?, tipo_eva?, nivel_eva?, banco_preg?, id_tutor?}} data
 * @returns {Promise<{id_eva, ...}>}
 */
export const actualizarEvaluacion = (id, data) =>
  api.put(`/api/evaluaciones/${id}`, data)

/**
 * Elimina una evaluación del sistema.
 * @param {number} id
 * @returns {Promise<string>}
 */
export const eliminarEvaluacion = (id) =>
  api.delete(`/api/evaluaciones/${id}`)
