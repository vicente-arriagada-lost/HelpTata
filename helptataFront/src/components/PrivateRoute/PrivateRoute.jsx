// =============================================================
// COMPONENTE PrivateRoute — PrivateRoute.jsx
// =============================================================
// Protege rutas que requieren autenticación o un rol específico.
//
// Flujo:
//   1. ¿No está autenticado? → redirige a /login guardando la URL
//      original en state.from para poder volver tras el login.
//   2. ¿Tiene roles requeridos pero no los cumple? → redirige a /
//   3. Pasa ambas → renderiza children.
//
// Uso:
//   <PrivateRoute>              → solo requiere estar logueado
//   <PrivateRoute roles={['ADMIN']}> → requiere rol ADMIN
// =============================================================
import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '../../hooks/useAuth'
import PropTypes from 'prop-types'

export default function PrivateRoute({ children, roles }) {
  const { isAuthenticated, hasRole } = useAuth()
  const location = useLocation()

  //* Si no está autenticado, guardar la ruta actual para redirigir después del login
  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />
  }

  //* Si se especificaron roles y el usuario no tiene ninguno de ellos → inicio
  if (roles && roles.length > 0 && !hasRole(roles)) {
    return <Navigate to="/" replace />
  }

  return children
}

PrivateRoute.propTypes = {
  children: PropTypes.node.isRequired,
  roles: PropTypes.arrayOf(PropTypes.string),
}
