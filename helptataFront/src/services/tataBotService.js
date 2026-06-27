import { crearCliente } from './axiosConfig'

const api = crearCliente(import.meta.env.VITE_MS_TATABOT_URL || 'http://localhost:8087')

export function enviarMensajeTataBot(mensaje, historial = []) {
  return api.post('/api/tatabot/chat', { mensaje, historial })
}
