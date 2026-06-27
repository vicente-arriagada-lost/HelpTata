// =============================================================
// FÁBRICA DE CLIENTES AXIOS — axiosConfig.js
// =============================================================
// En vez de tener un único cliente con una URL base fija,
// HelpTata usa VARIOS microservicios en distintos puertos.
// Esta función crea un cliente axios ya configurado para
// cualquier URL base que se le pase, evitando repetir la
// misma lógica de interceptores en cada servicio.
//
// Cada servicio llama a crearCliente(URL) una sola vez al
// cargarse el módulo, y a partir de ahí usa esa instancia
// para todas sus peticiones.
//
// INTERCEPTOR DE REQUEST (antes de enviar cada petición):
//   Lee el token JWT del localStorage (helptata_token) y lo
//   adjunta en el header Authorization como "Bearer <token>".
//   Cuando el backend implemente JWT, este interceptor lo
//   enviará automáticamente sin cambiar nada en los servicios.
//
// INTERCEPTOR DE RESPONSE (al recibir la respuesta):
//   Si el backend responde 401 (sesión expirada o token
//   inválido), limpia localStorage y redirige al login.
// =============================================================
import axios from 'axios'

/**
 * Crea y devuelve un cliente axios configurado con los
 * interceptores de autenticación para el backend de HelpTata.
 *
 * @param {string} baseURL - URL base del microservicio (ej: 'http://localhost:8080')
 * @returns {import('axios').AxiosInstance} Instancia axios lista para usar
 */
export function crearCliente(baseURL) {
  // Crea la instancia con URL base y tipo de contenido por defecto
  const cliente = axios.create({
    baseURL,
    headers: { 'Content-Type': 'application/json' },
  })

  // ── Interceptor de petición ────────────────────────────────
  // Lee el token del localStorage y lo adjunta si existe.
  // Esto cubre el flujo JWT cuando el backend lo implemente.
  cliente.interceptors.request.use(
    (config) => {
      const token = localStorage.getItem('helptata_token')
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
      return config
    },
    (error) => Promise.reject(error)
  )

  // ── Interceptor de respuesta ───────────────────────────────
  // Si el servidor devuelve 401, la sesión expiró o el token
  // es inválido: limpiar y redirigir al login.
  cliente.interceptors.response.use(
    (response) => response,
    (error) => {
      if (error.response?.status === 401) {
        localStorage.removeItem('helptata_token')
        localStorage.removeItem('helptata_user')
        window.location.href = '/login'
      }
      return Promise.reject(error)
    }
  )

  return cliente
}
