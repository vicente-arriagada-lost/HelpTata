// Convierte un porcentaje (0-100) a nota chilena (1.0-7.0)
// Fórmula: nota = (porcentaje / 100) * 6 + 1
export function porcentajeANota(pct) {
  const nota = (pct / 100) * 6 + 1
  return Math.round(nota * 10) / 10
}