// =============================================================
// SERVICIO DE USUARIOS — usuarioService.js
// =============================================================
// Gestión completa de usuarios, emails y roles del sistema.
// Apunta al microservicio ms-Usuario (puerto 8080).
//
// USUARIOS:
//   getUsuarios()            → GET    /api/usuarios
//   getUsuarioPorId(id)      → GET    /api/usuarios/{id}
//   actualizarUsuario(id, d) → PUT    /api/usuarios/{id}
//   eliminarUsuario(id)      → DELETE /api/usuarios/{id}
//
// EMAILS:
//   getEmails()              → GET    /api/emails
//   getEmailPorId(id)        → GET    /api/emails/{id}
//   actualizarEmail(id, d)   → PUT    /api/emails/{id}
//   eliminarEmail(id)        → DELETE /api/emails/{id}
//
// ROLES:
//   getRoles()               → GET    /api/roles
//   getRolPorId(id)          → GET    /api/roles/{id}
//   crearRol(data)           → POST   /api/roles
//   actualizarRol(id, data)  → PUT    /api/roles/{id}
//   eliminarRol(id)          → DELETE /api/roles/{id}
//
// Nota: login y register viven en authService.js ya que
// tienen lógica adicional (JWT, formato de datos).
// =============================================================
import { crearCliente } from './axiosConfig'

//* Cliente apuntando al microservicio de usuarios
const api = crearCliente(import.meta.env.VITE_MS_USUARIO_URL || 'http://localhost:8080')

// ── USUARIOS ──────────────────────────────────────────────────────────────────

/**
 * Obtiene la lista completa de usuarios del sistema (uso admin).
 * @returns {Promise<Array<{id_usuario, run_usuario, pnombre_usuario, papellido_usuario, email, id_rol}>>}
 */
export const getUsuarios = () =>
  api.get('/api/usuarios')

/**
 * Obtiene un usuario por su ID.
 * @param {number} id
 * @returns {Promise<{id_usuario, run_usuario, pnombre_usuario, papellido_usuario, email, id_rol}>}
 */
export const getUsuarioPorId = (id) =>
  api.get(`/api/usuarios/${id}`)

/**
 * Actualiza los datos de un usuario existente.
 * @param {number} id
 * @param {{pnombre_usuario?, snombre_usuario?, papellido_usuario?, sapellido_usuario?,
 *   fecha_nac_usuario?, telefono_usuario?, password_usuario?, id_direccion?, id_rol?}} data
 * @returns {Promise<{id_usuario, ...}>}
 */
export const actualizarUsuario = (id, data) =>
  api.put(`/api/usuarios/${id}`, data)

/**
 * Elimina un usuario del sistema.
 * @param {number} id
 * @returns {Promise<string>}
 */
export const eliminarUsuario = (id) =>
  api.delete(`/api/usuarios/${id}`)

// ── EMAILS ────────────────────────────────────────────────────────────────────

/**
 * Obtiene todos los emails registrados.
 * @returns {Promise<Array<{id_email, email}>>}
 */
export const getEmails = () =>
  api.get('/api/emails')

/**
 * Obtiene un email por su ID.
 * @param {number} id
 * @returns {Promise<{id_email, email}>}
 */
export const getEmailPorId = (id) =>
  api.get(`/api/emails/${id}`)

/**
 * Actualiza el email de un usuario.
 * @param {number} id
 * @param {{email: string}} data
 * @returns {Promise<{id_email, email}>}
 */
export const actualizarEmail = (id, data) =>
  api.put(`/api/emails/${id}`, data)

/**
 * Elimina un email del sistema.
 * @param {number} id
 * @returns {Promise<string>}
 */
export const eliminarEmail = (id) =>
  api.delete(`/api/emails/${id}`)

// ── ROLES ─────────────────────────────────────────────────────────────────────

/**
 * Obtiene todos los roles disponibles (ADMIN, USER, etc.).
 * Útil para poblar selects en formularios de administración.
 * @returns {Promise<Array<{id_rol, nombre_rol}>>}
 */
export const getRoles = () =>
  api.get('/api/roles')

/**
 * Obtiene un rol por su ID.
 * @param {number} id
 * @returns {Promise<{id_rol, nombre_rol}>}
 */
export const getRolPorId = (id) =>
  api.get(`/api/roles/${id}`)

/**
 * Crea un nuevo rol (uso admin).
 * @param {{nombre_rol: string}} data
 * @returns {Promise<{id_rol, nombre_rol}>}
 */
export const crearRol = (data) =>
  api.post('/api/roles', data)

/**
 * Actualiza el nombre de un rol.
 * @param {number} id
 * @param {{nombre_rol: string}} data
 * @returns {Promise<{id_rol, nombre_rol}>}
 */
export const actualizarRol = (id, data) =>
  api.put(`/api/roles/${id}`, data)

/**
 * Elimina un rol del sistema.
 * @param {number} id
 * @returns {Promise<string>}
 */
export const eliminarRol = (id) =>
  api.delete(`/api/roles/${id}`)
