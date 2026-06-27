// =============================================================
// SERVICIO DE CUESTIONARIOS — cuestionarioService.js
// =============================================================
// Conecta con el microservicio ms-PreguntasRespuestas (8086)
// para obtener cuestionarios, preguntas, alternativas y
// enviar las respuestas del usuario.
//
// getCuestionariosPorTutorial(idTutor):
//   GET /api/cuestionarios/tutorial/{id} → cuestionarios de un tutorial
//
// getPreguntasPorCuestionario(idCuestionario):
//   GET /api/preguntas/cuestionario/{id} → preguntas del cuestionario
//
// getAlternativasPorPregunta(idPregunta):
//   GET /api/alternativas/pregunta/{id} → alternativas de cada pregunta
//
// responderCuestionario(idCuestionario, data):
//   POST /api/cuestionarios/{id}/responder
//   Envía respuestas, calcula puntaje y notifica a ms-Progreso.
//   Respuesta: { id_resultado, correctas, incorrectas, porcentaje }
// =============================================================
import { crearCliente } from './axiosConfig'

// Cliente apuntando al microservicio de preguntas y respuestas
const api = crearCliente(import.meta.env.VITE_MS_PREGUNTAS_URL || 'http://localhost:8086')

/**
 * Obtiene los cuestionarios asociados a un tutorial.
 * @param {number} idTutor
 * @returns {Promise<Array<{id_cuestionario, titulo_cuestionario, descripcion_cuestionario, id_tutor}>>}
 */
export const getCuestionariosPorTutorial = (idTutor) =>
  api.get(`/api/cuestionarios/tutorial/${idTutor}`)

/**
 * Obtiene las preguntas de un cuestionario específico.
 * @param {number} idCuestionario
 * @returns {Promise<Array<{id_pregunta, enunciado_pregunta, id_cuestionario}>>}
 */
export const getPreguntasPorCuestionario = (idCuestionario) =>
  api.get(`/api/preguntas/cuestionario/${idCuestionario}`)

/**
 * Obtiene las alternativas de respuesta de una pregunta.
 * @param {number} idPregunta
 * @returns {Promise<Array<{id_alternativa, texto_alternativa, es_correcta, id_pregunta}>>}
 */
export const getAlternativasPorPregunta = (idPregunta) =>
  api.get(`/api/alternativas/pregunta/${idPregunta}`)

/**
 * Envía las respuestas del usuario, obtiene la corrección y
 * notifica el progreso al microservicio ms-Progreso.
 *
 * @param {number} idCuestionario
 * @param {{
 *   id_usuario: number,
 *   respuestas: Array<{id_pregunta: number, id_alternativa_seleccionada: number}>
 * }} data
 * @returns {Promise<{id_resultado, correctas, incorrectas, porcentaje}>}
 */
export const responderCuestionario = (idCuestionario, data) =>
  api.post(`/api/cuestionarios/${idCuestionario}/responder`, data)
