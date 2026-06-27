// =============================================================
// SERVICIO DE AUTENTICACIÓN — authService.js
// =============================================================
// Maneja el login y el registro de usuarios comunicándose
// con el microservicio ms-Usuario (puerto 8080).
//
// login(email, password):
//   POST /api/usuarios/login → { id_usuario, pnombre_usuario,
//   papellido_usuario, email, rol, token }
//   Devuelve el JWT firmado en HS256 que se guarda en el AuthContext.
//
// register(formData):
//   Flujo de dos pasos:
//   1. POST /api/usuarios  → crea el usuario con sus datos
//   2. POST /api/emails    → asocia el correo al sistema
//   Nota de diseño: el backend vincula email con usuario
//   asumiendo que id_email == id_usuario (mismo auto-increment).
//   Esta convención funciona en entornos de datos limpios;
//   en producción deberá revisarse con una FK explícita.
// =============================================================
import { crearCliente } from './axiosConfig'

// Cliente apuntando al microservicio de usuarios
const api = crearCliente(import.meta.env.VITE_MS_USUARIO_URL || 'http://localhost:8080')

/**
 * Autentica un usuario por email y contraseña.
 * @param {string} email
 * @param {string} password
 * @returns {Promise<{id_usuario, pnombre_usuario, papellido_usuario, email}>}
 */
export const login = (email, password) =>
  api.post('/api/usuarios/login', { email, password })

/**
 * Registra un nuevo usuario en el sistema.
 * Convierte el formulario frontend al formato que espera el backend
 * y realiza dos peticiones: crear usuario + crear email.
 *
 * @param {{
 *   primerNombre: string,
 *   segundoNombre?: string,
 *   apellido: string,
 *   segundoApellido?: string,
 *   rut: string,           // formato "12.345.678-9"
 *   fechaNacimiento: string, // formato "DD/MM/AAAA"
 *   telefono: string,
 *   email: string,
 *   password: string
 * }} formData
 * @returns {Promise<object>} Datos del usuario creado
 */
export const register = async (formData) => {
  // Extraer cuerpo y dígito verificador del RUT formateado
  const rutLimpio = formData.rut.replace(/[^0-9kK]/g, '').toUpperCase()
  const run = parseInt(rutLimpio.slice(0, -1), 10)
  const dvrun = rutLimpio.slice(-1)

  // La fecha viene en formato YYYY-MM-DD desde input[type=date] — el backend la espera igual
  const fechaNac = formData.fechaNacimiento

  // Paso 1: crear el usuario
  // id_direccion: 1 es la dirección por defecto del sistema.
  // TODO: en una versión futura, permitir al usuario elegir su dirección
  // usando los endpoints de ms-Direccion (puerto 8084).
  const resUsuario = await api.post('/api/usuarios', {
    run_usuario: run,
    dvrun_usuario: dvrun,
    pnombre_usuario: formData.primerNombre,
    snombre_usuario: formData.segundoNombre || null,
    papellido_usuario: formData.apellido,
    sapellido_usuario: formData.segundoApellido || null,
    fecha_nac_usuario: fechaNac,
    telefono_usuario: formData.telefono,
    password_usuario: formData.password,
    id_direccion: 1,
  })

  // Paso 2: registrar el email en el sistema
  // El backend vincula email↔usuario asumiendo id_email == id_usuario.
  await api.post('/api/emails', { email: formData.email })

  return resUsuario.data
}
