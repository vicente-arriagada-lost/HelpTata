import { useEffect } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'

// Ruta destino de la app Android tras login exitoso.
// La app nativa llama: http://[web-url]/auth-callback?token=JWT
// Este componente lee el token, establece la sesión y redirige al inicio.
export default function AuthCallback() {
  const [params]   = useSearchParams()
  const { login }  = useAuth()
  const navigate   = useNavigate()

  useEffect(() => {
    const token = params.get('token')
    if (token) {
      login(token)
      navigate('/', { replace: true })
    } else {
      navigate('/login', { replace: true })
    }
  }, [])

  return (
    <div style={{
      minHeight: '100vh', display: 'flex',
      alignItems: 'center', justifyContent: 'center',
      background: 'linear-gradient(to bottom, #1e3a5f, #2d527a)'
    }}>
      <p style={{ color: '#fff', fontSize: '1.8rem', fontWeight: '600' }}>
        Iniciando sesión...
      </p>
    </div>
  )
}
