// =============================================================
// COMPONENTE Login — Login.jsx
// =============================================================
// Formulario de inicio de sesión. Valida el email y la
// contraseña, llama al authService para autenticar contra
// el backend (ms-Usuario, puerto 8080) y guarda la sesión
// en el AuthContext mediante el hook useAuth.
//
// Props:
//   onLogin    → callback que recibe los datos del usuario tras login exitoso
//   onNavigate → función para cambiar de página
// =============================================================
import { useState } from 'react'
import { AlertCircle, Eye, EyeOff } from 'lucide-react'
import { login as loginService } from '../../src/services/authService'
import { useAuth } from '../../src/hooks/useAuth'
import { validarEmail, MAX_EMAIL, MAX_PASS } from '../../src/validators/fieldValidators'
import styles from './Login.module.scss'

export function Login({ onLogin, onNavigate }) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [errors, setErrors] = useState({})
  const [showPassword, setShowPassword] = useState(false)
  // Indica si hay una petición al backend en curso (para deshabilitar el botón)
  const [loading, setLoading] = useState(false)

  // Función login del AuthContext para guardar la sesión
  const { login: guardarSesion } = useAuth()

  // Valida los campos del formulario antes de enviar
  const validate = () => {
    const newErrors = {}
    const emailError = validarEmail(email)
    if (emailError) newErrors.email = emailError
    if (!password) newErrors.password = 'La contraseña es obligatoria.'
    return newErrors
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    const validationErrors = validate()
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors)
      return
    }

    setLoading(true)
    setErrors({})

    try {
      // Llamada al backend: POST /api/usuarios/login
      const res = await loginService(email, password)

      // Guardar solo el token JWT — los datos del usuario se extraen del payload
      guardarSesion(res.data.token)

      // Notificar al componente padre para actualizar la navegación
      onLogin()
    } catch (err) {
      // Mostrar error de credenciales si el backend devuelve 401, o un error genérico
      const msg = err.response?.status === 401
        ? 'Correo o contraseña incorrectos.'
        : 'No se pudo conectar con el servidor. Intenta más tarde.'
      setErrors({ credentials: msg })
    } finally {
      setLoading(false)
    }
  }

  // Clase del input según si tiene error
  const inputClass = (hasError) =>
    `${styles.inputBase} ${hasError ? styles.inputError : styles.inputNormal} focus:ring-4 focus:ring-blue-200`

  return (
    <div className={`${styles.pageWrapper} min-h-screen flex items-center justify-center py-10 px-4 sm:px-6`}>
      <main
        id="main-content"
        className={`${styles.formCard} bg-white rounded-2xl shadow-2xl w-full`}
      >
        <h1 className={`${styles.heading} text-center font-bold mb-3`}>
          Iniciar Sesión
        </h1>
        <p className={`${styles.subtext} text-center mb-8`}>
          Accede a tu cuenta de HelpTata
        </p>

        <form onSubmit={handleSubmit} className="space-y-7" noValidate>

          {/* Error de credenciales (devuelto por el backend) */}
          {errors.credentials && (
            <div
              role="alert"
              className={`${styles.errorBox} flex items-center gap-3 rounded-xl px-4 py-3`}
            >
              <AlertCircle size={24} className={styles.errorIcon} aria-hidden="true" />
              <p className={styles.errorText}>
                {errors.credentials}
              </p>
            </div>
          )}

          {/* Campo de correo electrónico */}
          <div>
            <label htmlFor="login-email" className={`${styles.label} block font-semibold mb-3`}>
              Correo Electrónico <span aria-hidden="true" className="text-red-600">*</span>
            </label>
            <input
              id="login-email"
              type="email"
              value={email}
              onChange={(e) => { setEmail(e.target.value); setErrors(prev => ({ ...prev, email: '', credentials: '' })) }}
              className={inputClass(!!errors.email || !!errors.credentials)}
              placeholder="tu@email.com"
              autoComplete="email"
              maxLength={MAX_EMAIL}
              aria-required="true"
              aria-describedby={errors.email ? 'login-email-error' : undefined}
              aria-invalid={!!errors.email}
            />
            {errors.email && (
              <p id="login-email-error" role="alert" className={`${styles.fieldError} mt-2 flex items-center gap-2`}>
                <AlertCircle size={20} aria-hidden="true" />
                {errors.email}
              </p>
            )}
          </div>

          {/* Campo de contraseña */}
          <div>
            <label htmlFor="login-password" className={`${styles.label} block font-semibold mb-3`}>
              Contraseña <span aria-hidden="true" className="text-red-600">*</span>
            </label>
            <div className={styles.passwordWrapper}>
              <input
                id="login-password"
                type={showPassword ? 'text' : 'password'}
                value={password}
                onChange={(e) => { setPassword(e.target.value); setErrors(prev => ({ ...prev, password: '', credentials: '' })) }}
                className={`${inputClass(!!errors.password || !!errors.credentials)} ${styles.passwordInput}`}
                placeholder="••••••••"
                autoComplete="current-password"
                maxLength={MAX_PASS}
                aria-required="true"
                aria-describedby={errors.password ? 'login-password-error' : undefined}
                aria-invalid={!!errors.password}
              />
              <button
                type="button"
                onClick={() => setShowPassword(v => !v)}
                className={styles.toggleBtn}
                aria-label={showPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'}
                tabIndex={-1}
              >
                {showPassword ? <EyeOff size={22} /> : <Eye size={22} />}
              </button>
            </div>
            {errors.password && (
              <p id="login-password-error" role="alert" className={`${styles.fieldError} mt-2 flex items-center gap-2`}>
                <AlertCircle size={20} aria-hidden="true" />
                {errors.password}
              </p>
            )}
          </div>

          {/* Botón de submit: deshabilitado mientras carga */}
          <button
            type="submit"
            disabled={loading}
            className={`${styles.submitBtn} w-full rounded-xl hover:opacity-90 transition-opacity focus:outline-none focus:ring-4 focus:ring-offset-2 disabled:opacity-60 disabled:cursor-not-allowed`}
          >
            {loading ? 'Ingresando...' : 'Iniciar Sesión'}
          </button>
        </form>

        {/* Link para ir al registro */}
        <div className="mt-8 text-center">
          <p className={styles.footerText}>
            ¿No tienes cuenta?{' '}
            <button
              onClick={() => onNavigate('register')}
              className={`${styles.footerLink} underline hover:opacity-80 focus:outline-none focus:ring-2 focus:ring-blue-400 rounded`}
            >
              Regístrate aquí
            </button>
          </p>
        </div>
      </main>
    </div>
  )
}
