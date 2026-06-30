import styles from './ResultadoReview.module.scss'

export function ResultadoReview({ course, onVolver }) {
  const raw = localStorage.getItem(`helptata_quiz_${course?.id}`)

  if (!raw) {
    return (
      <div className={`${styles.pageWrapper} min-h-screen flex items-center justify-center px-4`}>
        <div className="bg-white rounded-2xl shadow-2xl p-10 text-center max-w-lg">
          <p className={styles.emptyText}>No hay respuestas guardadas para este curso.</p>
          <button onClick={onVolver} className={`${styles.volverBtn} mt-6 rounded-xl`}>
            ← Volver al Curso
          </button>
        </div>
      </div>
    )
  }

  const { preguntas, answers, nota, correctas, incorrectas } = JSON.parse(raw)
  const aprobado = nota >= 4.0

  return (
    <div className={`${styles.pageWrapper} min-h-screen py-10 px-4 sm:px-6`}>
      <main id="main-content" className="max-w-4xl mx-auto">
        <button onClick={onVolver} className={`${styles.volverBtn} mb-8 rounded-xl`}>
          ← Volver al Curso
        </button>

        {/* Resumen */}
        <div className="bg-white rounded-2xl shadow-2xl p-8 sm:p-12 mb-8 text-center">
          <h1 className={`${styles.heading} font-bold mb-2`}>Revisión de Respuestas</h1>
          <p className={styles.subheading}>{course.title}</p>
          <div className="flex justify-center gap-8 mt-6 flex-wrap">
            <div className={`${styles.statBox} rounded-xl p-5`} style={{ borderColor: aprobado ? '#86efac' : '#fca5a5' }}>
              <p className={styles.statLabel}>Tu Nota</p>
              <p className="font-bold" style={{ fontSize: '2.5rem', color: aprobado ? '#16a34a' : '#d97706' }}>{nota}</p>
              <p className={styles.statSub}>de 7.0</p>
            </div>
            <div className={`${styles.statBox} rounded-xl p-5`} style={{ borderColor: '#86efac' }}>
              <p className={styles.statLabel}>Correctas</p>
              <p className="font-bold" style={{ fontSize: '2.5rem', color: '#16a34a' }}>{correctas}</p>
            </div>
            <div className={`${styles.statBox} rounded-xl p-5`} style={{ borderColor: '#fca5a5' }}>
              <p className={styles.statLabel}>Incorrectas</p>
              <p className="font-bold" style={{ fontSize: '2.5rem', color: '#dc2626' }}>{incorrectas}</p>
            </div>
          </div>
        </div>

        {/* Preguntas detalladas */}
        <div className="space-y-8">
          {preguntas.map((pregunta, qi) => {
            const respuesta = answers[qi]
            const correcta = pregunta.options.find(o => o.esCorrecta)
            const seleccionada = pregunta.options.find(o => o.id === respuesta?.id_alternativa_seleccionada)
            const acerto = respuesta?.correcto

            return (
              <div key={pregunta.id} className="bg-white rounded-2xl shadow-lg p-6 sm:p-10">
                {/* Número y enunciado */}
                <div className="flex items-start gap-4 mb-6">
                  <span
                    className="flex-shrink-0 w-10 h-10 rounded-full flex items-center justify-center font-bold text-white text-lg"
                    style={{ backgroundColor: acerto ? '#16a34a' : '#dc2626' }}
                    aria-hidden="true"
                  >
                    {qi + 1}
                  </span>
                  <h2 className={`${styles.questionText} font-bold leading-relaxed`}>
                    {pregunta.question}
                  </h2>
                </div>

                {/* Opciones */}
                <div className="space-y-3">
                  {pregunta.options.map((option, oi) => {
                    const esSel = option.id === respuesta?.id_alternativa_seleccionada
                    const esCorr = option.esCorrecta

                    let bg = '#f9fafb'
                    let border = '#d0dae8'
                    let color = '#1a1a2e'
                    let icono = null
                    let nota_extra = null

                    if (esSel && acerto) {
                      // Seleccionó correctamente → verde
                      bg = '#f0fdf4'; border = '#86efac'; color = '#14532d'
                      icono = '✓'
                    } else if (esSel && !acerto) {
                      // Seleccionó incorrectamente → rojo
                      bg = '#fef2f2'; border = '#fca5a5'; color = '#7f1d1d'
                      icono = '✗'
                      nota_extra = 'Esta respuesta es incorrecta'
                    } else if (!esSel && esCorr && !acerto) {
                      // Es la correcta que no seleccionó → azul
                      bg = '#eff6ff'; border = '#93c5fd'; color = '#1e3a5f'
                      icono = '✓'
                      nota_extra = 'Esta es la respuesta correcta'
                    }

                    if (!esSel && !(esCorr && !acerto)) return (
                      <div
                        key={option.id}
                        className="rounded-xl px-5 py-4"
                        style={{ backgroundColor: bg, border: `2px solid ${border}`, color }}
                      >
                        <span className="font-semibold mr-2">{['A', 'B', 'C', 'D'][oi]})</span>
                        {option.texto}
                      </div>
                    )

                    return (
                      <div
                        key={option.id}
                        className="rounded-xl px-5 py-4"
                        style={{ backgroundColor: bg, border: `2px solid ${border}`, color }}
                      >
                        <div className="flex items-center gap-3 font-bold" style={{ fontSize: '1.15rem' }}>
                          <span aria-hidden="true">{icono}</span>
                          <span>
                            <span className="mr-2">{['A', 'B', 'C', 'D'][oi]})</span>
                            {option.texto}
                          </span>
                        </div>
                        {nota_extra && (
                          <p className="mt-2 ml-7" style={{ fontSize: '1.05rem', opacity: 0.85 }}>
                            {nota_extra}
                            {!acerto && esCorr && correcta && (
                              <> — La alternativa correcta es: <strong>{correcta.texto}</strong></>
                            )}
                          </p>
                        )}
                      </div>
                    )
                  })}
                </div>
              </div>
            )
          })}
        </div>

        <button onClick={onVolver} className={`${styles.volverBtn} mt-10 w-full rounded-xl`}>
          ← Volver al Curso
        </button>
      </main>
    </div>
  )
}
