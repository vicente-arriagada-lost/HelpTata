// =============================================================
// HOOK useAuth — useAuth.js
// =============================================================
// Punto de acceso seguro al contexto de autenticación.
// En vez de llamar useContext(AuthContext) directamente en
// cada componente, usas este hook: si alguien lo llama fuera
// de AuthProvider, el error que lanza es claro en vez de un
// crash silencioso con "Cannot read properties of null".
//
// Devuelve: { token, usuario, login, logout, isAuthenticated }
// — todo lo que AuthContext expone, disponible en una línea.
//
// Uso:
//   const { usuario, isAuthenticated, logout } = useAuth()
// =============================================================
import { useContext } from 'react'
import { AuthContext } from '../context/AuthContext'

/**
 * Hook para consumir el contexto de autenticación de HelpTata.
 * Debe usarse dentro de un componente envuelto por AuthProvider.
 *
 * @returns {{ token: string|null, usuario: object|null, login: Function, logout: Function, isAuthenticated: boolean }}
 */
export function useAuth() {
  const context = useContext(AuthContext)

  // Si el hook se usa fuera del AuthProvider, lanzar un error descriptivo
  if (!context) {
    throw new Error('useAuth debe usarse dentro de <AuthProvider>')
  }

  return context
}
