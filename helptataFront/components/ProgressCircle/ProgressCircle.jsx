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

// percentage: 0-100 para calcular el arco
// nota: si se entrega, se muestra en el centro en vez del %
export function ProgressCircle({ percentage, nota }) {
  const radius = 70
  const circumference = 2 * Math.PI * radius
  const offset = circumference - (percentage / 100) * circumference

  return (
    <div
      className={`${styles.wrapper} relative`}
      role="img"
      aria-label={nota != null ? `Nota del curso: ${nota}` : `Progreso del curso: ${percentage} por ciento`}
    >
      <svg
        className={styles.svg}
        viewBox="0 0 160 160"
        aria-hidden="true"
        focusable="false"
      >
        <circle cx="80" cy="80" r={radius} stroke="#e5e7eb" strokeWidth="14" fill="transparent" />
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

      <div className="absolute inset-0 flex items-center justify-center" aria-hidden="true">
        {nota != null ? (
          <div className="flex flex-col items-center leading-none">
            <span className={`${styles.pctNumber} font-bold`} style={{ fontSize: '2.2rem' }}>{nota}</span>
            <span className={styles.pctSymbol} style={{ fontSize: '1rem' }}>nota</span>
          </div>
        ) : (
          <div className="flex items-baseline gap-1">
            <span className={`${styles.pctNumber} font-bold`}>{percentage}</span>
            <span className={styles.pctSymbol}>%</span>
          </div>
        )}
      </div>
    </div>
  )
}
