// =============================================================
// COMPONENTE Register — Register.jsx
// =============================================================
// Formulario de registro de nuevo usuario. Recopila los datos
// personales, los valida en el frontend con los validators y
// llama a authService.register() para crear la cuenta en el
// backend (ms-Usuario, puerto 8080).
//
// Flujo del registro:
//   1. Validación local de todos los campos
//   2. POST /api/usuarios → crea el usuario
//   3. POST /api/emails   → registra el correo en el sistema
//   4. Redirige al login si todo sale bien
//
// Props:
//   onRegister → callback tras registro exitoso
//   onNavigate → función para cambiar de página
// =============================================================
import { useState } from 'react'
import { AlertCircle, CheckCircle2, Circle, Eye, EyeOff } from 'lucide-react'
import { login as loginService, register as registerService } from '../../src/services/authService'
import { useAuth } from '../../src/hooks/useAuth'
import { formatRut, validarRutChileno, validarFechaNac, MIN_FECHA, MAX_FECHA } from '../../src/validators/rutValidators'
import { validarEmail, validarPassword, validarConfirmPassword, validarTelefono, passwordRules, MAX_NOMBRE, MAX_EMAIL, MAX_PASS } from '../../src/validators/fieldValidators'
import styles from './Register.module.scss'

// Componente auxiliar para mostrar mensajes de error con icono
function ErrorMsg({ id, msg }) {
  if (!msg) return null
  return (
    <p id={id} role="alert" className={`${styles.fieldError} mt-2 flex items-center gap-2`}>
      <AlertCircle size={20} aria-hidden="true" />
      {msg}
    </p>
  )
}

export function Register({ onRegister, onNavigate }) {
  // Estado del formulario con todos los campos requeridos
  const [formData, setFormData] = useState({
    primerNombre: '',
    segundoNombre: '',
    apellido: '',
    segundoApellido: '',
    rut: '',
    fechaNacimiento: '',
    telefono: '',
    email: '',
    password: '',
    confirmPassword: '',
  })
  const [errors, setErrors] = useState({})
  const [loading, setLoading] = useState(false)
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)
  // Controla si el usuario ya tocó el campo de confirmación (para mostrar el error de inmediato)
  const [confirmTouched, setConfirmTouched] = useState(false)

  // Función del AuthContext para guardar la sesión tras el auto-login
  const { login } = useAuth()

  // Indica si las contraseñas coinciden mientras el usuario escribe
  const passwordsMatch = formData.confirmPassword === '' || formData.password === formData.confirmPassword
  const showMismatch = confirmTouched && formData.confirmPassword !== '' && !passwordsMatch

  // Maneja cambios en todos los campos; formatea RUT y filtra teléfono en tiempo real
  const handleChange = (e) => {
    const { name, value } = e.target
    let newValue = value
    if (name === 'rut') newValue = formatRut(value)
    if (name === 'telefono') {
      // Solo permite dígitos y + al inicio
      const hasPlus = value.startsWith('+')
      newValue = (hasPlus ? '+' : '') + value.replace(/[^0-9]/g, '')
    }
    setFormData(prev => ({ ...prev, [name]: newValue }))
    setErrors(prev => ({ ...prev, [name]: '' }))
  }

  // Maneja el campo de confirmación de contraseña por separado
  const handleConfirmChange = (e) => {
    setFormData(prev => ({ ...prev, confirmPassword: e.target.value }))
    setConfirmTouched(true)
    setErrors(prev => ({ ...prev, confirmPassword: '' }))
  }

  // Valida todos los campos y retorna un objeto de errores (vacío si todo es válido)
  const validate = () => {
    const e = {}

    // Nombres obligatorios: no vacíos y máximo 40 caracteres
    if (!formData.primerNombre.trim()) {
      e.primerNombre = 'El primer nombre es obligatorio.'
    } else if (formData.primerNombre.length > MAX_NOMBRE) {
      e.primerNombre = `El primer nombre no puede superar ${MAX_NOMBRE} caracteres.`
    }

    if (formData.segundoNombre && formData.segundoNombre.length > MAX_NOMBRE) {
      e.segundoNombre = `El segundo nombre no puede superar ${MAX_NOMBRE} caracteres.`
    }

    if (!formData.apellido.trim()) {
      e.apellido = 'El apellido paterno es obligatorio.'
    } else if (formData.apellido.length > MAX_NOMBRE) {
      e.apellido = `El apellido no puede superar ${MAX_NOMBRE} caracteres.`
    }

    if (formData.segundoApellido && formData.segundoApellido.length > MAX_NOMBRE) {
      e.segundoApellido = `El segundo apellido no puede superar ${MAX_NOMBRE} caracteres.`
    }

    // Validar RUT usando el validador con algoritmo módulo 11
    if (!formData.rut.trim()) {
      e.rut = 'El RUT es obligatorio.'
    } else if (!validarRutChileno(formData.rut)) {
      e.rut = 'Ingresa un RUT válido (ej: 12.345.678-9).'
    }

    // Validar fecha de nacimiento: mayor de 18 y nacido desde 1900
    const fechaError = validarFechaNac(formData.fechaNacimiento)
    if (fechaError) e.fechaNacimiento = fechaError

    const telefonoError = validarTelefono(formData.telefono)
    if (telefonoError) e.telefono = telefonoError

    const emailError = validarEmail(formData.email)
    if (emailError) e.email = emailError

    const passError = validarPassword(formData.password)
    if (passError) e.password = passError

    const confirmError = validarConfirmPassword(formData.password, formData.confirmPassword)
    if (confirmError) e.confirmPassword = confirmError

    return e
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    const validationErrors = validate()
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors)
      setConfirmTouched(true)
      // Hacer foco en el primer campo con error
      const firstKey = Object.keys(validationErrors)[0]
      document.getElementById(`reg-${firstKey}`)?.focus()
      return
    }

    setLoading(true)
    setErrors({})

    try {
      // Paso 1: crear el usuario y registrar el email en el backend
      await registerService(formData)

      // Paso 2: iniciar sesión automáticamente con las credenciales recién creadas
      const loginRes = await loginService(formData.email, formData.password)
      login(loginRes.data.token)

      // Paso 3: ir a la página principal ya autenticado
      onNavigate('main')
    } catch (err) {
      const msg = err.response?.data?.message || 'No se pudo completar el registro. Intenta más tarde.'
      setErrors({ general: msg })
    } finally {
      setLoading(false)
    }
  }

  // Clase del input según si tiene error
  const inputClass = (hasError) =>
    `${styles.inputBase} ${hasError ? styles.inputError : styles.inputNormal} focus:ring-4 focus:ring-blue-200`

  return (
    <div className={`${styles.pageWrapper} min-h-screen flex items-center justify-center py-10 px-4 sm:px-6`}>
      <main id="main-content" className={`${styles.formCard} bg-white rounded-2xl shadow-2xl w-full`}>
        <h1 className={`${styles.heading} text-center font-bold mb-2`}>
          Crear Cuenta
        </h1>
        <p className={`${styles.subtext} text-center mb-7`}>
          Únete a HelpTata y comienza a aprender
        </p>

        {/* Error general del backend */}
        {errors.general && (
          <div role="alert" className={`${styles.errorBox} flex items-center gap-3 rounded-xl px-4 py-3 mb-5`}>
            <AlertCircle size={24} className={styles.errorIcon} aria-hidden="true" />
            <p className={styles.errorText}>{errors.general}</p>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-5" noValidate>

          {/* ── Nombres ── */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
            <div>
              <label htmlFor="reg-primerNombre" className={`${styles.label} block mb-2`}>
                Primer Nombre <span aria-hidden="true" className="text-red-600">*</span>
              </label>
              <input id="reg-primerNombre" type="text" name="primerNombre" value={formData.primerNombre} onChange={handleChange}
                className={inputClass(!!errors.primerNombre)} placeholder="Juan" autoComplete="given-name"
                aria-required="true" aria-describedby={errors.primerNombre ? 'err-primerNombre' : undefined}
                aria-invalid={!!errors.primerNombre} maxLength={MAX_NOMBRE} />
              <ErrorMsg id="err-primerNombre" msg={errors.primerNombre} />
            </div>
            <div>
              <label htmlFor="reg-segundoNombre" className={`${styles.label} block mb-2`}>Segundo Nombre</label>
              <input id="reg-segundoNombre" type="text" name="segundoNombre" value={formData.segundoNombre} onChange={handleChange}
                className={inputClass(!!errors.segundoNombre)} placeholder="Carlos" autoComplete="additional-name"
                maxLength={MAX_NOMBRE} />
              <p className={styles.hint}>(opcional)</p>
              <ErrorMsg id="err-segundoNombre" msg={errors.segundoNombre} />
            </div>
          </div>

          {/* ── Apellidos ── */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
            <div>
              <label htmlFor="reg-apellido" className={`${styles.label} block mb-2`}>
                Apellido <span aria-hidden="true" className="text-red-600">*</span>
              </label>
              <input id="reg-apellido" type="text" name="apellido" value={formData.apellido} onChange={handleChange}
                className={inputClass(!!errors.apellido)} placeholder="Pérez" autoComplete="family-name"
                aria-required="true" aria-invalid={!!errors.apellido} maxLength={MAX_NOMBRE} />
              <ErrorMsg id="err-apellido" msg={errors.apellido} />
            </div>
            <div>
              <label htmlFor="reg-segundoApellido" className={`${styles.label} block mb-2`}>Segundo Apellido</label>
              <input id="reg-segundoApellido" type="text" name="segundoApellido" value={formData.segundoApellido} onChange={handleChange}
                className={inputClass(!!errors.segundoApellido)} placeholder="González" autoComplete="family-name"
                maxLength={MAX_NOMBRE} />
              <p className={styles.hint}>(opcional)</p>
              <ErrorMsg id="err-segundoApellido" msg={errors.segundoApellido} />
            </div>
          </div>

          {/* ── RUT ── */}
          <div>
            <label htmlFor="reg-rut" className={`${styles.label} block mb-2`}>
              RUT <span aria-hidden="true" className="text-red-600">*</span>
            </label>
            <input id="reg-rut" type="text" name="rut" value={formData.rut} onChange={handleChange}
              className={inputClass(!!errors.rut)} placeholder="12.345.678-9" autoComplete="off"
              aria-required="true" aria-describedby={`hint-rut${errors.rut ? ' err-rut' : ''}`}
              aria-invalid={!!errors.rut} maxLength={12} />
            <ErrorMsg id="err-rut" msg={errors.rut} />
            <p id="hint-rut" className={styles.hint}>Escribe tu RUT y se formateará solo (ej: 12.345.678-9).</p>
          </div>

          {/* ── Fecha de nacimiento + Teléfono ── */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
            <div>
              <label htmlFor="reg-fechaNacimiento" className={`${styles.label} block mb-2`}>
                Fecha de Nacimiento <span aria-hidden="true" className="text-red-600">*</span>
              </label>
              <input id="reg-fechaNacimiento" type="date" name="fechaNacimiento" value={formData.fechaNacimiento} onChange={handleChange}
                className={inputClass(!!errors.fechaNacimiento)} autoComplete="bday"
                min={MIN_FECHA} max={MAX_FECHA}
                aria-required="true" aria-describedby={`hint-fecha${errors.fechaNacimiento ? ' err-fecha' : ''}`}
                aria-invalid={!!errors.fechaNacimiento} />
              <ErrorMsg id="err-fecha" msg={errors.fechaNacimiento} />
              <p id="hint-fecha" className={styles.hint}>Debes tener al menos 18 años. Solo fechas desde 1900.</p>
            </div>
            <div>
              <label htmlFor="reg-telefono" className={`${styles.label} block mb-2`}>
                Número de Teléfono <span aria-hidden="true" className="text-red-600">*</span>
              </label>
              <input id="reg-telefono" type="tel" name="telefono" value={formData.telefono} onChange={handleChange}
                className={inputClass(!!errors.telefono)} placeholder="+56912345678" autoComplete="tel"
                aria-required="true" aria-invalid={!!errors.telefono} maxLength={12} />
              <ErrorMsg id="err-telefono" msg={errors.telefono} />
              <p id="hint-telefono" className={styles.hint}>Solo números y + al inicio (ej: +56912345678).</p>
            </div>
          </div>

          {/* ── Email ── */}
          <div>
            <label htmlFor="reg-email" className={`${styles.label} block mb-2`}>
              Correo Electrónico <span aria-hidden="true" className="text-red-600">*</span>
            </label>
            <input id="reg-email" type="email" name="email" value={formData.email} onChange={handleChange}
              className={inputClass(!!errors.email)} placeholder="tu@email.com" autoComplete="email"
              aria-required="true" aria-describedby={`hint-email${errors.email ? ' err-email' : ''}`}
              aria-invalid={!!errors.email} maxLength={MAX_EMAIL} />
            <ErrorMsg id="err-email" msg={errors.email} />
            <p id="hint-email" className={styles.hint}>Este correo se usará para iniciar sesión.</p>
          </div>

          {/* ── Contraseña con indicadores de requisitos ── */}
          <div>
            <label htmlFor="reg-password" className={`${styles.label} block mb-2`}>
              Contraseña <span aria-hidden="true" className="text-red-600">*</span>
            </label>
            <div className={styles.passwordWrapper}>
              <input id="reg-password" type={showPassword ? 'text' : 'password'} name="password" value={formData.password} onChange={handleChange}
                className={`${inputClass(!!errors.password)} ${styles.passwordInput}`} placeholder="••••••••" autoComplete="new-password"
                aria-required="true" aria-describedby="hint-password" aria-invalid={!!errors.password} maxLength={MAX_PASS} />
              <button type="button" onClick={() => setShowPassword(v => !v)} className={styles.toggleBtn}
                aria-label={showPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'} tabIndex={-1}>
                {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
              </button>
            </div>
            <ErrorMsg id="err-password" msg={errors.password} />
            {/* Indicadores visuales de cada requisito — color dinámico se mantiene inline */}
            <ul id="hint-password" className="mt-3 space-y-2" aria-label="Requisitos de contraseña">
              {passwordRules.map(rule => {
                const met = formData.password.length > 0 && rule.test(formData.password)
                return (
                  <li key={rule.key} className="flex items-center gap-2"
                    style={{ fontSize: '1.25rem', color: met ? '#15803d' : '#6b7280', fontWeight: met ? '600' : '400', transition: 'color 0.2s' }}
                    aria-label={`${rule.label}: ${met ? 'cumplido' : 'pendiente'}`}>
                    {met
                      ? <CheckCircle2 size={22} style={{ color: '#15803d', flexShrink: 0 }} aria-hidden="true" />
                      : <Circle size={22} style={{ color: '#d1d5db', flexShrink: 0 }} aria-hidden="true" />}
                    {rule.label}
                  </li>
                )
              })}
            </ul>
          </div>

          {/* ── Confirmar contraseña ── */}
          <div>
            <label htmlFor="reg-confirmPassword" className={`${styles.label} block mb-2`}>
              Confirmar Contraseña <span aria-hidden="true" className="text-red-600">*</span>
            </label>
            <div className={styles.passwordWrapper}>
              <input id="reg-confirmPassword" type={showConfirmPassword ? 'text' : 'password'} name="confirmPassword" value={formData.confirmPassword}
                onChange={handleConfirmChange} className={`${inputClass(showMismatch || !!errors.confirmPassword)} ${styles.passwordInput}`}
                placeholder="••••••••" autoComplete="new-password" aria-required="true" maxLength={MAX_PASS}
                aria-describedby={(showMismatch || errors.confirmPassword) ? 'err-confirm' : undefined}
                aria-invalid={showMismatch || !!errors.confirmPassword} />
              <button type="button" onClick={() => setShowConfirmPassword(v => !v)} className={styles.toggleBtn}
                aria-label={showConfirmPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'} tabIndex={-1}>
                {showConfirmPassword ? <EyeOff size={20} /> : <Eye size={20} />}
              </button>
            </div>
            {(showMismatch || errors.confirmPassword) && (
              <p id="err-confirm" role="alert" className={`${styles.fieldError} mt-2 flex items-center gap-2`}>
                <AlertCircle size={20} aria-hidden="true" />
                {errors.confirmPassword || 'Las contraseñas no coinciden.'}
              </p>
            )}
          </div>

          <p className={styles.requiredNote}>
            Los campos marcados con <span className="text-red-600 font-bold">*</span> son obligatorios.
          </p>

          {/* Botón de submit */}
          <button
            type="submit"
            disabled={loading}
            className={`${styles.submitBtn} w-full rounded-xl hover:opacity-90 transition-opacity focus:outline-none focus:ring-4 focus:ring-offset-2 disabled:opacity-60 disabled:cursor-not-allowed`}
          >
            {loading ? 'Creando cuenta...' : 'Crear Cuenta'}
          </button>
        </form>

        {/* Link para ir al login */}
        <div className="mt-6 text-center">
          <p className={styles.footerText}>
            ¿Ya tienes cuenta?{' '}
            <button
              onClick={() => onNavigate('login')}
              className={`${styles.footerLink} underline hover:opacity-80 focus:outline-none focus:ring-2 focus:ring-blue-400 rounded`}
            >
              Inicia sesión aquí
            </button>
          </p>
        </div>
      </main>
    </div>
  )
}
