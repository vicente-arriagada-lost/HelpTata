// =============================================================
// VALIDADORES DE CAMPOS DE FORMULARIO — fieldValidators.js
// =============================================================
// Funciones de validación reutilizables para los formularios
// de HelpTata. Se usan directamente en los handlers de change
// y submit de cada componente.
//
// ── Funciones de validación directa (retornan string|null) ──
//   validarEmail(email)              → formato válido con @ y dominio
//   validarPassword(password)        → mínimo 8 chars, mayúscula y símbolo
//   validarConfirmPassword(p, c)     → ambas contraseñas coinciden
//   validarTelefono(telefono)        → formato chileno o internacional
//   validarNombre(nombre, etiqueta)  → campo no vacío ni solo espacios
//
// ── Constantes de límites ────────────────────────────────────
//   MAX_NOMBRE = 20   MAX_EMAIL = 40   MIN_PASS = 8   MAX_PASS = 30
//
// ── Utilidades de validación (retornan boolean|string) ───────
//   noSoloEspacios(v)   → 'No puede contener solo espacios' o true
//
// ── passwordRules ─────────────────────────────────────────────
//   Lista de reglas con key, label y función test para mostrar
//   los requisitos de contraseña en tiempo real en el formulario.
// =============================================================

// ─── Límites de campos ────────────────────────────────────────────────────────
export const MAX_NOMBRE = 40
export const MAX_EMAIL  = 100
export const MIN_PASS   = 8
export const MAX_PASS   = 20

// Patrón de email: local@dominio.tld — cubre gmail, hotmail, etc.
const PATRON_EMAIL = /^[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9][a-zA-Z0-9.\-]*\.[a-zA-Z]{2,}$/

// ─── Utilidad genérica ───────────────────────────────────────────────────────

/**
 * Valida que un campo no contenga solo espacios en blanco.
 * @param {string} v - Valor del campo
 * @returns {true|string} true si pasa, mensaje de error si falla
 */
export const noSoloEspacios = (v) =>
  !v || v.trim().length > 0 || 'No puede contener solo espacios'

// ─── Validadores directos (retornan string|null) ─────────────────────────────

/**
 * Valida un correo electrónico.
 * @param {string} email
 * @returns {string|null} Mensaje de error, o null si es válido
 */
export function validarEmail(email) {
  if (!email || !email.trim()) return 'El correo electrónico es obligatorio.'
  if (email.length > MAX_EMAIL) return `El correo no puede superar ${MAX_EMAIL} caracteres.`
  if (!PATRON_EMAIL.test(email)) return 'Ingresa un correo electrónico válido.'
  return null
}

/**
 * Verifica que una contraseña cumpla los requisitos mínimos de HelpTata:
 * mínimo 8 caracteres, sin espacios, con mayúscula, minúscula, número y símbolo.
 * @param {string} password
 * @returns {string|null} Mensaje de error, o null si es válida
 */
export function validarPassword(password) {
  if (!password) return 'La contraseña es obligatoria.'
  if (password.length < MIN_PASS) return `La contraseña debe tener al menos ${MIN_PASS} caracteres.`
  if (password.length > MAX_PASS) return `La contraseña no puede superar ${MAX_PASS} caracteres.`
  if (/\s/.test(password)) return 'La contraseña no puede contener espacios.'
  if (!/[a-z]/.test(password)) return 'Debe incluir al menos una letra minúscula.'
  if (!/[A-Z]/.test(password)) return 'Debe incluir al menos una letra mayúscula.'
  if (!/[0-9]/.test(password)) return 'Debe incluir al menos un número.'
  if (!/[^a-zA-Z0-9\s]/.test(password)) return 'Debe incluir al menos un símbolo (ej: !@#$).'
  return null
}

/**
 * Valida que ambas contraseñas coincidan.
 * @param {string} password
 * @param {string} confirmacion
 * @returns {string|null} Mensaje de error, o null si coinciden
 */
export function validarConfirmPassword(password, confirmacion) {
  if (!confirmacion) return 'Debes confirmar tu contraseña.'
  if (password !== confirmacion) return 'Las contraseñas no coinciden.'
  return null
}

/**
 * Valida un número de teléfono (formato chileno e internacional).
 * @param {string} telefono
 * @returns {string|null} Mensaje de error, o null si es válido
 */
export function validarTelefono(telefono) {
  if (!telefono || !String(telefono).trim()) return 'El número de teléfono es obligatorio.'
  if (!/^\+?[0-9]{7,11}$/.test(String(telefono).trim())) {
    return 'Solo se permiten números y el signo + al inicio (ej: +56912345678).'
  }
  return null
}

/**
 * Valida que un campo de nombre no esté vacío ni solo con espacios,
 * y no exceda MAX_NOMBRE caracteres.
 * @param {string} nombre
 * @param {string} [etiqueta='El campo'] - Nombre del campo para el mensaje de error
 * @returns {string|null} Mensaje de error, o null si es válido
 */
export function validarNombre(nombre, etiqueta = 'El campo') {
  if (!nombre || !nombre.trim()) return `${etiqueta} es obligatorio.`
  if (nombre.length > MAX_NOMBRE) return `${etiqueta} no puede superar ${MAX_NOMBRE} caracteres.`
  return null
}

// ─── passwordRules ─────────────────────────────────────────────────────────────
/**
 * Lista de reglas de contraseña para mostrar en tiempo real al usuario.
 * Cada entrada tiene: key (único), label (texto visible) y test (función).
 */
export const passwordRules = [
  { key: 'len',     label: `Mínimo ${MIN_PASS} caracteres`,        test: (p) => p.length >= MIN_PASS },
  { key: 'lower',   label: 'Al menos 1 letra minúscula',           test: (p) => /[a-z]/.test(p) },
  { key: 'upper',   label: 'Al menos 1 letra mayúscula',           test: (p) => /[A-Z]/.test(p) },
  { key: 'number',  label: 'Al menos 1 número',                    test: (p) => /[0-9]/.test(p) },
  { key: 'symbol',  label: 'Al menos 1 símbolo (ej: !@#$)',        test: (p) => /[^a-zA-Z0-9\s]/.test(p) },
  { key: 'spaces',  label: 'Sin espacios',                         test: (p) => !/\s/.test(p) },
]
