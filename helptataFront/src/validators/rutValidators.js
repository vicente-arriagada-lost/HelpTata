// =============================================================
// VALIDADORES DE RUT CHILENO — rutValidators.js
// =============================================================
// Utilidades para validar y formatear el RUT en formularios.
//
// formatRut(valor):
//   Formatea el input del usuario en tiempo real al escribir.
//   Ejemplo: "123456789" → "12.345.678-9"
//
// validarRutChileno(rut):
//   Verifica que el RUT tenga formato correcto Y que el dígito
//   verificador sea matemáticamente válido usando el algoritmo
//   módulo 11 del Servicio de Registro Civil de Chile (SRCeI).
//
//   Algoritmo módulo 11:
//   Multiplica cada dígito del cuerpo por 2,3,4,5,6,7 en ciclo
//   (de derecha a izquierda), suma los productos, calcula el
//   resto de dividir por 11 y lo compara con el dígito verificador.
//   Resto 0 → dv '0' | Resto 1 → dv 'K' | Otro → 11 - resto.
//
// esMayorDe18(fechaStr):
//   Valida que la persona tenga al menos 18 años cumplidos.
//   Recibe la fecha en formato "DD/MM/AAAA".
//
// MAX_FECHA:
//   Constante calculada dinámicamente (hoy - 18 años) en formato
//   "YYYY-MM-DD" para usarla como atributo max en input[type=date],
//   así el selector de fecha no permite elegir a un menor de edad.
// =============================================================

/**
 * Formatea un RUT mientras el usuario escribe, insertando puntos y guion.
 * Acepta caracteres numéricos y K/k.
 *
 * @param {string} valor - Valor crudo del input
 * @returns {string} RUT formateado: "12.345.678-9"
 */
export function formatRut(valor) {
  // Eliminar todo lo que no sea número o K
  const limpio = valor.replace(/[^0-9kK]/g, '').toUpperCase()
  if (limpio.length <= 1) return limpio

  // Separar cuerpo (todos menos el último) y dígito verificador (último)
  const cuerpo = limpio.slice(0, -1)
  const dv = limpio.slice(-1)

  // Agregar puntos al cuerpo cada 3 dígitos desde la derecha
  const cuerpoFormateado = cuerpo.replace(/\B(?=(\d{3})+(?!\d))/g, '.')
  return `${cuerpoFormateado}-${dv}`
}

/**
 * Valida si un RUT chileno tiene formato y dígito verificador correctos.
 * Acepta: "12.345.678-9" | "12345678-9" | "100000001-K"
 *
 * @param {string} rut - RUT en cualquier formato
 * @returns {boolean} true si el RUT es válido
 */
export function validarRutChileno(rut) {
  if (!rut) return false

  // Limpiar puntos y espacios, convertir a mayúsculas
  const limpio = rut.replace(/\./g, '').replace(/\s/g, '').toUpperCase()

  // Verificar formato básico: cuerpo de 7-9 dígitos + guion + dígito verificador
  if (!/^\d{7,9}-[0-9K]$/.test(limpio)) return false

  const [cuerpo, dv] = limpio.split('-')

  // Calcular dígito verificador esperado con el algoritmo módulo 11
  let suma = 0
  let multiplo = 2
  for (let i = cuerpo.length - 1; i >= 0; i--) {
    suma += parseInt(cuerpo[i]) * multiplo
    // El multiplicador cicla entre 2 y 7
    multiplo = multiplo < 7 ? multiplo + 1 : 2
  }
  const resto = suma % 11
  const dvEsperado = resto === 0 ? '0' : resto === 1 ? 'K' : String(11 - resto)

  return dv === dvEsperado
}

/**
 * Verifica que una persona tenga al menos 18 años cumplidos.
 *
 * @param {string} fechaStr - Fecha en formato "DD/MM/AAAA"
 * @returns {boolean} true si la persona tiene 18 años o más
 */
export function esMayorDe18(fechaStr) {
  if (!fechaStr || fechaStr.length < 10) return false
  const [dia, mes, anio] = fechaStr.split('/')
  if (!dia || !mes || !anio) return false

  const hoy = new Date()
  const nac = new Date(Number(anio), Number(mes) - 1, Number(dia))

  let edad = hoy.getFullYear() - nac.getFullYear()
  const diffMes = hoy.getMonth() - nac.getMonth()
  // Si aún no ha llegado el mes/día de cumpleaños este año, restar un año
  if (diffMes < 0 || (diffMes === 0 && hoy.getDate() < nac.getDate())) {
    edad--
  }
  return edad >= 18
}

// Fecha mínima: solo se aceptan fechas desde el año 1900 en adelante.
export const MIN_FECHA = '1900-01-01'

// Fecha máxima permitida en input[type=date] para garantizar mayoría de edad.
// Se calcula dinámicamente: hoy - 18 años, formato YYYY-MM-DD.
const _hoy = new Date()
_hoy.setFullYear(_hoy.getFullYear() - 18)
export const MAX_FECHA = _hoy.toISOString().split('T')[0]

/**
 * Valida una fecha de nacimiento en formato ISO (YYYY-MM-DD).
 * Verifica que la persona tenga al menos 18 años y haya nacido desde 1900.
 *
 * @param {string} fechaISO - Fecha en formato "YYYY-MM-DD" (valor de input[type=date])
 * @returns {string|null} Mensaje de error, o null si es válida
 */
export function validarFechaNac(fechaISO) {
  if (!fechaISO) return 'La fecha de nacimiento es obligatoria.'

  const nac = new Date(fechaISO + 'T00:00:00')
  if (isNaN(nac.getTime())) return 'Fecha de nacimiento inválida.'
  if (nac < new Date('1900-01-01')) return 'La fecha debe ser a partir del año 1900.'

  const hoy = new Date()
  let edad = hoy.getFullYear() - nac.getFullYear()
  const diffMes = hoy.getMonth() - nac.getMonth()
  if (diffMes < 0 || (diffMes === 0 && hoy.getDate() < nac.getDate())) edad--
  if (edad < 18) return 'Debes tener al menos 18 años para registrarte.'

  return null
}
