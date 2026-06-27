import axios from 'axios'

const BASE_URL = 'http://localhost:8087/api/tatabot'

export function enviarMensajeTataBot(mensaje, historial = []) {
  return axios.post(`${BASE_URL}/chat`, { mensaje, historial })
}
