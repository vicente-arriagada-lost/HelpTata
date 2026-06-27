// =============================================================
// CONTEXTO DE AUTENTICACIÓN — AuthContext.jsx
// =============================================================
// Guarda quién está logueado y lo deja disponible para cualquier
// componente sin necesidad de pasar props a mano por toda la jerarquía.
//
// Expone:
//   token          → JWT firmado por el backend (ms-Usuario)
//   usuario        → { id, nombre, email, rol } — extraído del JWT
//   login(token)   → guarda el JWT en localStorage y decodifica los datos
//   logout()       → limpia el token del localStorage
//   isAuthenticated → true si hay token válido
//   hasRole(rol)   → true si usuario.rol coincide con el rol pedido
//
// Escucha activa del JWT — tres mecanismos:
//   1. Al iniciar: si el token guardado ya expiró, se limpia antes de renderizar
//   2. Timer exacto: al hacer login se programa un setTimeout hasta el "exp" del JWT
//   3. visibilitychange: al volver a la pestaña se re-verifica si el token sigue vivo
//      (cubre laptops en suspensión o pestañas inactivas durante horas)
// En todos los casos: logout() + redirect a /login.
// =============================================================
import { createContext, useState, useCallback, useEffect, useRef } from 'react'
import PropTypes from 'prop-types'

export const AuthContext = createContext(null)

// Decodifica el payload de un JWT (base64url → objeto JSON).
// Usa TextDecoder para soportar nombres con tildes y caracteres especiales.
function decodeToken(token) {
  try {
    const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')
    const bytes = Uint8Array.from(atob(base64), c => c.charCodeAt(0))
    return JSON.parse(new TextDecoder().decode(bytes))
  } catch {
    return null
  }
}

// Construye el objeto de usuario a partir del payload del JWT
function usuarioDesdePayload(payload) {
  if (!payload) return null
  return {
    id: payload.id,
    nombre: payload.nombre,
    email: payload.sub,
    rol: payload.rol,
  }
}

// Retorna true si el token ya venció (exp es Unix segundos)
function tokenExpirado(token) {
  const payload = decodeToken(token)
  if (!payload?.exp) return true
  return payload.exp * 1000 < Date.now()
}

// Lee el token de localStorage y lo descarta si ya expiró
function leerTokenValido() {
  const t = localStorage.getItem('helptata_token')
  if (!t) return null
  if (tokenExpirado(t)) {
    localStorage.removeItem('helptata_token')
    localStorage.removeItem('helptata_user')
    return null
  }
  return t
}

export function AuthProvider({ children }) {
  // Lazy initializer: al montar, descarta el token si ya expiró
  const [token, setToken] = useState(leerTokenValido)
  const [usuario, setUsuario] = useState(() => usuarioDesdePayload(decodeToken(leerTokenValido())))

  // Referencia al setTimeout de expiración (para poder cancelarlo en logout)
  const expTimerRef = useRef(null)

  const clearExpTimer = useCallback(() => {
    if (expTimerRef.current) {
      clearTimeout(expTimerRef.current)
      expTimerRef.current = null
    }
  }, [])

  const logout = useCallback(() => {
    clearExpTimer()
    localStorage.removeItem('helptata_token')
    localStorage.removeItem('helptata_user')
    setToken(null)
    setUsuario(null)
    // Avisa al WebView de Android para limpiar el token cifrado
    window.Android?.logout?.()
  }, [clearExpTimer])

  // Programa el auto-logout exactamente cuando vence el "exp" del JWT
  const programarExpiracion = useCallback((tkn) => {
    clearExpTimer()
    const payload = decodeToken(tkn)
    if (!payload?.exp) return
    const msRestantes = payload.exp * 1000 - Date.now()
    if (msRestantes <= 0) {
      logout()
      window.location.href = '/login'
      return
    }
    expTimerRef.current = setTimeout(() => {
      logout()
      window.location.href = '/login'
    }, msRestantes)
  }, [clearExpTimer, logout])

  const login = useCallback((nuevoToken) => {
    localStorage.setItem('helptata_token', nuevoToken)
    localStorage.removeItem('helptata_user')
    const payload = decodeToken(nuevoToken)
    setToken(nuevoToken)
    setUsuario(usuarioDesdePayload(payload))
    programarExpiracion(nuevoToken)
  }, [programarExpiracion])

  // Al montar: si hay token válido, programar su expiración
  useEffect(() => {
    const t = leerTokenValido()
    if (t) programarExpiracion(t)
    return clearExpTimer
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  // Polling activo: cada 10s verifica que el token siga en localStorage y no haya expirado.
  // Solo se activa cuando hay sesión iniciada (token !== null).
  useEffect(() => {
    if (!token) return
    const intervalo = setInterval(() => {
      const t = localStorage.getItem('helptata_token')
      if (!t || tokenExpirado(t)) {
        logout()
        window.location.href = '/login'
      }
    }, 10000)
    return () => clearInterval(intervalo)
  }, [token, logout])

  // Evento storage: detecta cambios de localStorage desde OTRAS pestañas/ventanas.
  // Solo se activa cuando hay sesión iniciada.
  useEffect(() => {
    if (!token) return
    function handleStorage(e) {
      if (e.key === 'helptata_token' && !e.newValue) {
        logout()
        window.location.href = '/login'
      }
    }
    window.addEventListener('storage', handleStorage)
    return () => window.removeEventListener('storage', handleStorage)
  }, [token, logout])

  // Al volver a la pestaña: re-verificar si el token expiró mientras estaba en segundo plano.
  // Solo se activa cuando hay sesión iniciada.
  useEffect(() => {
    if (!token) return
    function handleVisibilidad() {
      if (document.visibilityState !== 'visible') return
      const t = localStorage.getItem('helptata_token')
      if (!t || tokenExpirado(t)) {
        logout()
        window.location.href = '/login'
      }
    }
    document.addEventListener('visibilitychange', handleVisibilidad)
    return () => document.removeEventListener('visibilitychange', handleVisibilidad)
  }, [token, logout])

  const isAuthenticated = !!token && !!usuario

  const hasRole = useCallback((rol) => {
    if (!usuario) return false
    if (Array.isArray(rol)) return rol.includes(usuario.rol)
    return usuario.rol === rol
  }, [usuario])

  return (
    <AuthContext.Provider value={{ token, usuario, login, logout, isAuthenticated, hasRole }}>
      {children}
    </AuthContext.Provider>
  )
}

AuthProvider.propTypes = {
  children: PropTypes.node.isRequired,
}
