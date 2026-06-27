// =============================================================
// TESTS — rutValidators.test.js
// =============================================================
// Pruebas unitarias para:
//   formatRut        — formateo visual del RUT con puntos y guion
//   validarRutChileno — validación del dígito verificador (módulo 11)
//   esMayorDe18      — comprueba mayoría de edad desde DD/MM/AAAA
//   MAX_FECHA        — constante para limitar el input[type=date]
//
// Ejecutar con: npm test
// =============================================================
import { describe, it, expect } from 'vitest'
import { formatRut, validarRutChileno, esMayorDe18, MAX_FECHA } from './rutValidators'

// ─── formatRut ────────────────────────────────────────────────────────────────
describe('formatRut', () => {
  it('formatea un RUT de 8 dígitos correctamente', () => {
    //* '123456789' → últimos dígito es el DV → '12.345.678-9'
    expect(formatRut('123456789')).toBe('12.345.678-9')
  })

  it('formatea un RUT corto de 7 dígitos', () => {
    expect(formatRut('1234567K')).toBe('123.456-7K')
  })

  it('acepta K como dígito verificador y lo convierte a mayúscula', () => {
    //* La k minúscula debe normalizarse a K mayúscula
    expect(formatRut('9876543k')).toBe('987.654-3K')
  })

  it('ignora caracteres no numéricos ni K antes de formatear', () => {
    //* Los puntos y guiones que el usuario ingresa manualmente se eliminan antes
    //* de procesar el RUT, por eso el resultado es igual al formateado limpio
    expect(formatRut('12.345.678-9')).toBe('12.345.678-9')
  })

  it('retorna el carácter solo si tiene longitud 1', () => {
    //* Un solo carácter no puede partirse en cuerpo + DV
    expect(formatRut('5')).toBe('5')
  })

  it('retorna vacío para input vacío', () => {
    expect(formatRut('')).toBe('')
  })
})

// ─── validarRutChileno ────────────────────────────────────────────────────────
describe('validarRutChileno', () => {
  it('acepta un RUT válido con puntos y guion', () => {
    //* 12.345.678-9 es un RUT con DV correcto según el algoritmo módulo 11
    expect(validarRutChileno('12.345.678-9')).toBe(true)
  })

  it('acepta un RUT válido sin puntos', () => {
    //* El usuario puede ingresar el RUT sin formateo — ambas formas deben funcionar
    expect(validarRutChileno('12345678-9')).toBe(true)
  })

  it('acepta RUT con dígito verificador K', () => {
    //* 17.263.753-K es un RUT válido real — K es el resultado cuando módulo 11 da 10
    expect(validarRutChileno('17.263.753-K')).toBe(true)
  })

  it('rechaza un RUT con dígito verificador incorrecto', () => {
    //* El DV correcto para 12345678 es 9, no 0
    expect(validarRutChileno('12.345.678-0')).toBe(false)
  })

  it('rechaza un RUT sin guion', () => {
    //* Sin guion no se puede separar el cuerpo del DV → formato inválido
    expect(validarRutChileno('123456789')).toBe(false)
  })

  it('rechaza un RUT con cuerpo muy corto', () => {
    //* Los RUTs chilenos tienen al menos 7 dígitos en el cuerpo
    expect(validarRutChileno('123456-9')).toBe(false)
  })

  it('rechaza null o undefined', () => {
    //* El campo podría llegar vacío desde el formulario — no debe lanzar excepción
    expect(validarRutChileno(null)).toBe(false)
    expect(validarRutChileno(undefined)).toBe(false)
    expect(validarRutChileno('')).toBe(false)
  })
})

// ─── esMayorDe18 ─────────────────────────────────────────────────────────────
describe('esMayorDe18', () => {
  it('retorna true para alguien con exactamente 18 años cumplidos', () => {
    //* Calculamos la fecha dinámica para que el test no expire el año que viene
    const hoy = new Date()
    const fecha = new Date(hoy.getFullYear() - 18, hoy.getMonth(), hoy.getDate())
    const dd   = String(fecha.getDate()).padStart(2, '0')
    const mm   = String(fecha.getMonth() + 1).padStart(2, '0')  // getMonth() es base-0
    const aaaa = fecha.getFullYear()
    //* La función espera el formato DD/MM/AAAA — separador debe ser barra
    expect(esMayorDe18(`${dd}/${mm}/${aaaa}`)).toBe(true)
  })

  it('retorna true para alguien mayor de 18 años', () => {
    expect(esMayorDe18('15/03/1990')).toBe(true)
  })

  it('retorna false para un menor de edad (nacido hace 17 años)', () => {
    //* Construido dinámicamente para que el test sea válido en cualquier año
    const hoy = new Date()
    const fecha = new Date(hoy.getFullYear() - 17, hoy.getMonth(), hoy.getDate())
    const dd   = String(fecha.getDate()).padStart(2, '0')
    const mm   = String(fecha.getMonth() + 1).padStart(2, '0')
    const aaaa = fecha.getFullYear()
    expect(esMayorDe18(`${dd}/${mm}/${aaaa}`)).toBe(false)
  })

  it('retorna false para string vacío', () => {
    //* Campo no rellenado — no debe lanzar excepción
    expect(esMayorDe18('')).toBe(false)
  })

  it('retorna false para formato incorrecto (AAAA-MM-DD en vez de DD/MM/AAAA)', () => {
    //* La función solo acepta DD/MM/AAAA — el formato ISO causa un parsed inválido
    expect(esMayorDe18('1990-03-15')).toBe(false)
  })
})

// ─── MAX_FECHA ────────────────────────────────────────────────────────────────
describe('MAX_FECHA', () => {
  it('tiene formato YYYY-MM-DD', () => {
    //* El atributo max de input[type=date] requiere este formato exacto
    expect(MAX_FECHA).toMatch(/^\d{4}-\d{2}-\d{2}$/)
  })

  it('corresponde a hace 18 años (año actual - 18)', () => {
    //* Se usa como max en el input de fecha de nacimiento para bloquear menores
    const año = new Date().getFullYear() - 18
    expect(MAX_FECHA.startsWith(String(año))).toBe(true)
  })
})
