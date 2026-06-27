// =============================================================
// SERVICIO DE UBICACIÓN — ubicacionService.js
// =============================================================
// Gestión de la jerarquía geográfica y las direcciones de usuarios.
// Apunta al microservicio ms-Direccion (puerto 8084).
//
// La geografía funciona en cascada:
//   País → Región → Ciudad → Comuna → Dirección
//
// PAÍSES:
//   getPaises()              → GET /api/paises
//   getPaisPorId(id)         → GET /api/paises/{id}
//
// REGIONES:
//   getRegiones()            → GET /api/regiones
//   getRegionesPorPais(id)   → GET /api/regiones/pais/{id}
//
// CIUDADES:
//   getCiudades()            → GET /api/ciudades
//   getCiudadesPorRegion(id) → GET /api/ciudades/region/{id}
//
// COMUNAS:
//   getComunas()             → GET /api/comunas
//   getComunasPorCiudad(id)  → GET /api/comunas/ciudad/{id}
//   getComunaPorId(id)       → GET /api/comunas/{id}
//
// DIRECCIONES:
//   getDirecciones()             → GET    /api/direcciones
//   getDireccionPorId(id)        → GET    /api/direcciones/{id}
//   getDireccionesPorComuna(id)  → GET    /api/direcciones/comuna/{id}
//   crearDireccion(data)         → POST   /api/direcciones
//   actualizarDireccion(id, d)   → PUT    /api/direcciones/{id}
//   eliminarDireccion(id)        → DELETE /api/direcciones/{id}
// =============================================================
import { crearCliente } from './axiosConfig'

//* Cliente apuntando al microservicio de dirección
const api = crearCliente(import.meta.env.VITE_MS_DIRECCION_URL || 'http://localhost:8084')

// ── PAÍSES ────────────────────────────────────────────────────────────────────

/**
 * Obtiene todos los países disponibles.
 * @returns {Promise<Array<{id_pais, nombre_pais}>>}
 */
export const getPaises = () =>
  api.get('/api/paises')

/**
 * Obtiene un país por su ID.
 * @param {number} id
 * @returns {Promise<{id_pais, nombre_pais}>}
 */
export const getPaisPorId = (id) =>
  api.get(`/api/paises/${id}`)

// ── REGIONES ─────────────────────────────────────────────────────────────────

/**
 * Obtiene todas las regiones del sistema.
 * @returns {Promise<Array<{id_region, nombre_region, id_pais}>>}
 */
export const getRegiones = () =>
  api.get('/api/regiones')

/**
 * Obtiene las regiones de un país específico.
 * Úsalo para poblar el select de regiones cuando el usuario elige el país.
 * @param {number} idPais
 * @returns {Promise<Array<{id_region, nombre_region, id_pais}>>}
 */
export const getRegionesPorPais = (idPais) =>
  api.get(`/api/regiones/pais/${idPais}`)

// ── CIUDADES ──────────────────────────────────────────────────────────────────

/**
 * Obtiene todas las ciudades del sistema.
 * @returns {Promise<Array<{id_ciudad, nombre_ciudad, id_region}>>}
 */
export const getCiudades = () =>
  api.get('/api/ciudades')

/**
 * Obtiene las ciudades de una región específica.
 * Úsalo para poblar el select de ciudades cuando el usuario elige la región.
 * @param {number} idRegion
 * @returns {Promise<Array<{id_ciudad, nombre_ciudad, id_region}>>}
 */
export const getCiudadesPorRegion = (idRegion) =>
  api.get(`/api/ciudades/region/${idRegion}`)

// ── COMUNAS ───────────────────────────────────────────────────────────────────

/**
 * Obtiene todas las comunas del sistema.
 * @returns {Promise<Array<{id_comuna, nombre_comuna, id_ciudad}>>}
 */
export const getComunas = () =>
  api.get('/api/comunas')

/**
 * Obtiene las comunas de una ciudad específica.
 * Úsalo para poblar el select de comunas cuando el usuario elige la ciudad.
 * @param {number} idCiudad
 * @returns {Promise<Array<{id_comuna, nombre_comuna, id_ciudad}>>}
 */
export const getComunasPorCiudad = (idCiudad) =>
  api.get(`/api/comunas/ciudad/${idCiudad}`)

/**
 * Obtiene una comuna por su ID.
 * @param {number} id
 * @returns {Promise<{id_comuna, nombre_comuna, id_ciudad}>}
 */
export const getComunaPorId = (id) =>
  api.get(`/api/comunas/${id}`)

// ── DIRECCIONES ───────────────────────────────────────────────────────────────

/**
 * Obtiene todas las direcciones del sistema (uso admin).
 * @returns {Promise<Array<{id_direccion, calle, numero, id_comuna}>>}
 */
export const getDirecciones = () =>
  api.get('/api/direcciones')

/**
 * Obtiene una dirección por su ID.
 * @param {number} id
 * @returns {Promise<{id_direccion, calle, numero, id_comuna}>}
 */
export const getDireccionPorId = (id) =>
  api.get(`/api/direcciones/${id}`)

/**
 * Obtiene las direcciones de una comuna específica.
 * @param {number} idComuna
 * @returns {Promise<Array<{id_direccion, calle, numero, id_comuna}>>}
 */
export const getDireccionesPorComuna = (idComuna) =>
  api.get(`/api/direcciones/comuna/${idComuna}`)

/**
 * Crea una nueva dirección.
 * @param {{calle: string, numero: string, id_comuna: number}} data
 * @returns {Promise<{id_direccion, calle, numero, id_comuna}>}
 */
export const crearDireccion = (data) =>
  api.post('/api/direcciones', data)

/**
 * Actualiza una dirección existente.
 * @param {number} id
 * @param {{calle?: string, numero?: string, id_comuna?: number}} data
 * @returns {Promise<{id_direccion, calle, numero, id_comuna}>}
 */
export const actualizarDireccion = (id, data) =>
  api.put(`/api/direcciones/${id}`, data)

/**
 * Elimina una dirección del sistema.
 * @param {number} id
 * @returns {Promise<string>}
 */
export const eliminarDireccion = (id) =>
  api.delete(`/api/direcciones/${id}`)
