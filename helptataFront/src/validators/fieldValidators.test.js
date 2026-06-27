// =============================================================
// TESTS — fieldValidators.test.js
// =============================================================
// Pruebas unitarias para todos los validadores de campos de
// formulario: email, contraseña, confirmación, teléfono y nombre.
//
// Convención de retorno:
//   null          → válido (sin error)
//   string        → mensaje de error a mostrar al usuario
//   true          → válido (patrón de react-hook-form para noSoloEspacios)
//   string distinto de true → error (patrón react-hook-form)
//
// Ejecutar con: npm test
// =============================================================
import { describe, it, expect } from 'vitest'
import {
  validarEmail,
  validarPassword,
  validarConfirmPassword,
  validarTelefono,
  validarNombre,
  noSoloEspacios,
  passwordRules,
  MAX_NOMBRE,
  MAX_EMAIL,
  MIN_PASS,
} from './fieldValidators'

// ─── validarEmail ────────────────────────────────────────────────────────────
describe('validarEmail', () => {
  it('retorna null para un email válido', () => {
    //* null significa sin error — el campo pasa la validación
    expect(validarEmail('usuario@gmail.com')).toBeNull()
    expect(validarEmail('test.user+tag@empresa.cl')).toBeNull()
  })

  it('retorna error si el email está vacío', () => {
    expect(validarEmail('')).not.toBeNull()
    expect(validarEmail('   ')).not.toBeNull()
  })

  it('retorna error si falta el @', () => {
    expect(validarEmail('usuariogmail.com')).not.toBeNull()
  })

  it('retorna error si falta el dominio después del @', () => {
    expect(validarEmail('usuario@')).not.toBeNull()
  })

  it('retorna error si falta la extensión (.com, .cl, etc.)', () => {
    //* La extensión de dominio es obligatoria — 'gmail' sin '.com' es inválido
    expect(validarEmail('usuario@gmail')).not.toBeNull()
  })

  it(`retorna error si supera ${MAX_EMAIL} caracteres`, () => {
    //* Evita que emails muy largos rompan el campo en BD (VARCHAR)
    expect(validarEmail(`${'a'.repeat(MAX_EMAIL)}@test.com`)).not.toBeNull()
  })
})

// ─── validarPassword ─────────────────────────────────────────────────────────
describe('validarPassword', () => {
  it('retorna null para una contraseña que cumple todos los requisitos', () => {
    //* Al menos 8 caracteres, minúscula, mayúscula, número y símbolo
    expect(validarPassword('Segura1!')).toBeNull()
    expect(validarPassword('HelpTata@2024')).toBeNull()
  })

  it('retorna error si la contraseña está vacía o es null', () => {
    //* null puede llegar si el campo no fue tocado por el usuario
    expect(validarPassword('')).not.toBeNull()
    expect(validarPassword(null)).not.toBeNull()
  })

  it(`retorna error si tiene menos de ${MIN_PASS} caracteres`, () => {
    expect(validarPassword('Ab1!')).not.toBeNull()
  })

  it('retorna error si no tiene minúscula', () => {
    expect(validarPassword('SEGURA1!')).not.toBeNull()
  })

  it('retorna error si no tiene mayúscula', () => {
    expect(validarPassword('segura1!')).not.toBeNull()
  })

  it('retorna error si no tiene número', () => {
    expect(validarPassword('Segura!!')).not.toBeNull()
  })

  it('retorna error si no tiene símbolo especial', () => {
    expect(validarPassword('Segura123')).not.toBeNull()
  })

  it('retorna error si contiene espacios', () => {
    //* Los espacios en contraseñas pueden causar problemas de tokenización en JWT
    expect(validarPassword('Segura 1!')).not.toBeNull()
  })
})

// ─── validarConfirmPassword ──────────────────────────────────────────────────
describe('validarConfirmPassword', () => {
  it('retorna null si ambas contraseñas son iguales', () => {
    expect(validarConfirmPassword('Segura1!', 'Segura1!')).toBeNull()
  })

  it('retorna error si la confirmación está vacía', () => {
    expect(validarConfirmPassword('Segura1!', '')).not.toBeNull()
  })

  it('retorna error si las contraseñas no coinciden', () => {
    //* Comparación case-sensitive — 'segura1!' ≠ 'Segura1!'
    expect(validarConfirmPassword('Segura1!', 'Diferente1!')).not.toBeNull()
  })
})

// ─── validarTelefono ─────────────────────────────────────────────────────────
describe('validarTelefono', () => {
  it('retorna null para un teléfono chileno válido', () => {
    //* Acepta formato con código de país o solo el número móvil
    expect(validarTelefono('+56912345678')).toBeNull()
    expect(validarTelefono('912345678')).toBeNull()
  })

  it('retorna error si está vacío', () => {
    expect(validarTelefono('')).not.toBeNull()
    expect(validarTelefono('   ')).not.toBeNull()
  })

  it('retorna error si contiene letras', () => {
    expect(validarTelefono('9123abc78')).not.toBeNull()
  })

  it('acepta valor de tipo number (como llega desde el backend)', () => {
    //* El backend guarda el teléfono como INTEGER, así que al cargarlo en el form
    //* llega como number, no como string — el validador debe manejar ambos tipos
    expect(validarTelefono(912345678)).toBeNull()
  })
})

// ─── validarNombre ───────────────────────────────────────────────────────────
describe('validarNombre', () => {
  it('retorna null para un nombre válido', () => {
    expect(validarNombre('Juan')).toBeNull()
    expect(validarNombre('María José')).toBeNull()
  })

  it('retorna error si el nombre está vacío', () => {
    expect(validarNombre('')).not.toBeNull()
    expect(validarNombre('   ')).not.toBeNull()
  })

  it(`retorna error si supera ${MAX_NOMBRE} caracteres`, () => {
    //* Protege el largo del VARCHAR en la BD — el campo tiene límite de ${MAX_NOMBRE} chars
    expect(validarNombre('A'.repeat(MAX_NOMBRE + 1))).not.toBeNull()
  })

  it('incluye la etiqueta del campo en el mensaje de error', () => {
    //* El parámetro label permite mensajes contextuales: "El primer nombre es requerido"
    const error = validarNombre('', 'El primer nombre')
    expect(error).toContain('El primer nombre')
  })
})

// ─── noSoloEspacios ───────────────────────────────────────────────────────────
describe('noSoloEspacios', () => {
  it('retorna true para un valor con contenido real', () => {
    //* true = válido en el patrón de react-hook-form validate
    expect(noSoloEspacios('Hola')).toBe(true)
  })

  it('retorna mensaje de error para valor de solo espacios', () => {
    //* Un string de espacios no es contenido real — el usuario no ingresó nada útil
    expect(noSoloEspacios('   ')).not.toBe(true)
  })

  it('retorna true para undefined o vacío (campo opcional sin rellenar)', () => {
    //* Un campo opcional vacío no debe fallar por "solo espacios"
    //* La regla "required" ya maneja el caso de campo obligatorio vacío
    expect(noSoloEspacios('')).toBe(true)
    expect(noSoloEspacios(undefined)).toBe(true)
  })
})

// ─── passwordRules ───────────────────────────────────────────────────────────
describe('passwordRules', () => {
  it('contiene exactamente 6 reglas', () => {
    //* len, lower, upper, number, symbol, spaces — si cambia este número,
    //* también debe actualizarse el componente de feedback visual de contraseña
    expect(passwordRules).toHaveLength(6)
  })

  it('cada regla tiene key, label y función test', () => {
    //* Estructura requerida por el componente de feedback visual
    passwordRules.forEach((regla) => {
      expect(regla).toHaveProperty('key')
      expect(regla).toHaveProperty('label')
      expect(typeof regla.test).toBe('function')
    })
  })

  it('la regla de longitud (len) evalúa correctamente', () => {
    const reglaLen = passwordRules.find((r) => r.key === 'len')
    expect(reglaLen.test('AbCd1234!')).toBe(true)   // ≥ 8 caracteres → pasa
    expect(reglaLen.test('Ab1!')).toBe(false)         // 4 caracteres → falla
  })

  it('la regla de símbolo (symbol) pasa solo con caracteres especiales', () => {
    const reglaSym = passwordRules.find((r) => r.key === 'symbol')
    expect(reglaSym.test('Abcd1234!')).toBe(true)   // tiene '!' → pasa
    expect(reglaSym.test('Abcd1234')).toBe(false)    // sin símbolo → falla
  })

  it('la regla de espacios (spaces) falla si hay espacios en la contraseña', () => {
    const reglaSpaces = passwordRules.find((r) => r.key === 'spaces')
    expect(reglaSpaces.test('Abcd 1!')).toBe(false)  // espacio → falla
    expect(reglaSpaces.test('Abcd1!')).toBe(true)    // sin espacios → pasa
  })
})
