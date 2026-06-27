// =============================================================
// COMPONENTE ProgressCircle — ProgressCircle.jsx
// =============================================================
// Círculo de progreso SVG animado que muestra el porcentaje
// de avance de un usuario en un curso.
//
// Props:
//   percentage → número entre 0 y 100
// =============================================================
import styles from './ProgressCircle.module.scss'

export function ProgressCircle({ percentage }) {
  const radius = 70
  // Circunferencia del círculo: 2π × radio
  const circumference = 2 * Math.PI * radius
  // Desplazamiento del trazo: 0% = circunferencia completa cubierta, 100% = sin desplazamiento
  const offset = circumference - (percentage / 100) * circumference

  return (
    <div
      className={`${styles.wrapper} relative`}
      role="img"
      aria-label={`Progreso del curso: ${percentage} por ciento`}
    >
      {/* SVG rotado -90° para que el inicio sea en la parte superior */}
      <svg
        className={styles.svg}
        viewBox="0 0 160 160"
        aria-hidden="true"
        focusable="false"
      >
        {/* Círculo de fondo (gris) */}
        <circle cx="80" cy="80" r={radius} stroke="#e5e7eb" strokeWidth="14" fill="transparent" />
        {/* Círculo de progreso (verde), animado con CSS transition */}
        <circle
          cx="80"
          cy="80"
          r={radius}
          stroke="#16a34a"
          strokeWidth="14"
          fill="transparent"
          strokeDasharray={circumference}
          strokeDashoffset={offset}
          className={styles.progressCircle}
        />
      </svg>

      {/* Porcentaje centrado dentro del círculo */}
      <div className="absolute inset-0 flex items-center justify-center" aria-hidden="true">
        <div className="flex items-baseline gap-1">
          <span className={`${styles.pctNumber} font-bold`}>
            {percentage}
          </span>
          <span className={styles.pctSymbol}>%</span>
        </div>
      </div>
    </div>
  )
}
