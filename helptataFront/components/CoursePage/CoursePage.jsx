// =============================================================
// COMPONENTE CoursePage — CoursePage.jsx
// =============================================================
// Página de detalle de un curso. Muestra el video del tutorial,
// los controles de video como referencia, el círculo de progreso
// del usuario y el chatbot TataBot en el panel lateral.
//
// Props:
//   course     → objeto con datos del tutorial { title, fullDescription, videoUrl }
//   user       → usuario autenticado (del AuthContext)
//   progress   → porcentaje de progreso del usuario en este curso (0-100)
//   onNavigate → función para cambiar de página
// =============================================================
import { useState, useEffect, useRef } from 'react'
import Plyr from 'plyr'
import 'plyr/dist/plyr.css'
import { Play, Volume2, SkipBack, SkipForward, Maximize } from 'lucide-react'
import { Header } from '../Header/Header'
import { ProgressCircle } from '../ProgressCircle/ProgressCircle'
import { TataBot } from '../TataBot/TataBot'
import { porcentajeANota } from '../../src/utils/nota'
import styles from './CoursePage.module.scss'

const PLYR_OPTIONS = {
  controls: ['play-large', 'play', 'progress', 'current-time', 'mute', 'volume', 'settings', 'fullscreen'],
  settings: ['speed'],
  speed: { selected: 1, options: [0.5, 0.75, 1, 1.25, 1.5] },
  i18n: {
    play: 'Reproducir', pause: 'Pausar', mute: 'Silenciar', unmute: 'Activar sonido',
    enterFullscreen: 'Pantalla completa', exitFullscreen: 'Salir de pantalla completa',
    speed: 'Velocidad', normal: 'Normal', settings: 'Configuración',
  },
}

export function CoursePage({ course, user, progress, onNavigate }) {
  const nota = porcentajeANota(progress)
  const tieneRespuestas = !!localStorage.getItem(`helptata_quiz_${course?.id}`)
  const videoRef = useRef(null)
  const plyrRef = useRef(null)
  const [mostrarOverlay, setMostrarOverlay] = useState(true)

  const handlePlay = () => {
    setMostrarOverlay(false)
    plyrRef.current?.play()
  }

  useEffect(() => {
    if (!videoRef.current) return
    plyrRef.current = new Plyr(videoRef.current, PLYR_OPTIONS)
    return () => { plyrRef.current?.destroy(); plyrRef.current = null }
  }, [])

  return (
    <div className={`${styles.pageWrapper} min-h-screen`}>
      {/* Header con botón de volver */}
      <Header user={user} onNavigate={onNavigate} showBackButton={true} />

      <main id="main-content" className="max-w-6xl mx-auto py-10 sm:py-14 px-6 sm:px-8">

        {/* Título y descripción del curso */}
        <div className="mb-10 sm:mb-14">
          <h1 className={`${styles.heading} font-bold mb-5`}>
            {course.title}
          </h1>
          <p className={`${styles.subtext} leading-relaxed`}>
            {course.fullDescription}
          </p>
        </div>

        {/* Grid: columna izquierda (video + quiz) y columna derecha (progreso + bot) */}
        <div className="grid grid-cols-1 lg:grid-cols-5 gap-8 sm:gap-10">
          <div className="lg:col-span-3 space-y-8 sm:space-y-10">

            {/* Sección de video */}
            <section
              aria-labelledby="video-heading"
              className={`${styles.card} bg-white rounded-2xl p-6 sm:p-10`}
            >
              <h2 id="video-heading" className={`${styles.sectionHeading} font-bold mb-5`}>
                Video del Curso
              </h2>
              <div className={`${styles.videoWrapper} rounded-xl overflow-hidden shadow-lg mb-7`}>
                <video ref={videoRef} playsInline className={styles.videoEl}>
                  <source src={course.videoUrl} type="video/mp4" />
                </video>
                {mostrarOverlay && (
                  <div className={styles.videoOverlay}>
                    <button
                      onClick={handlePlay}
                      className={`${styles.videoPlayBtn} focus:outline-none focus:ring-4 focus:ring-white`}
                      aria-label="Reproducir video del curso"
                    >
                      ▶ Haga click para ver
                    </button>
                  </div>
                )}
              </div>

              {/* Referencia de controles del reproductor */}
              <div className={`${styles.controlsBox} rounded-xl p-5 sm:p-7`}>
                <h3 className={`${styles.controlsHeading} font-bold mb-2`}>
                  ¿Para qué sirven los botones del video?
                </h3>
                <p className={`${styles.controlsIntro} mb-5`}>
                  El video de arriba tiene sus propios botones para controlarlo. A continuación te mostramos
                  qué hace cada uno. <strong>Estos símbolos son solo una guía visual</strong>, no son botones — búscalos directamente en el video.
                </p>
                <ul className="space-y-4" aria-label="Guía de controles del video">
                  {[
                    { Icon: Play,        label: 'Reproducir / Pausar el video' },
                    { Icon: Volume2,     label: 'Ajustar el volumen' },
                    { Icon: SkipBack,    label: 'Retroceder en el video' },
                    { Icon: SkipForward, label: 'Adelantar en el video' },
                    { Icon: Maximize,    label: 'Ver el video en pantalla completa' },
                  ].map(({ Icon, label }) => (
                    <li key={label} className={`${styles.controlItem} flex items-center gap-4`}>
                      <div className={`${styles.controlIconBox} p-2 rounded-lg flex-shrink-0`} aria-hidden="true">
                        <Icon size={28} className={styles.controlIcon} />
                      </div>
                      <span className={styles.controlLabel}>{label}</span>
                    </li>
                  ))}
                </ul>
                <p className={`${styles.controlsTip} mt-5 italic`}>
                  💡 Consejo: Puedes pausar el video en cualquier momento para tomar notas
                </p>
              </div>
            </section>

            {/* Sección de prueba de conocimientos */}
            <section
              aria-labelledby="quiz-heading"
              className={`${styles.card} bg-white rounded-2xl p-6 sm:p-10`}
            >
              <h2 id="quiz-heading" className={`${styles.sectionHeading} font-bold mb-4`}>
                Prueba de Conocimientos
              </h2>
              <p className={`${styles.quizText} mb-8 leading-relaxed`}>
                Pon a prueba lo que has aprendido con nuestro cuestionario interactivo.
                Responde las preguntas y verifica tu comprensión del curso.
              </p>
              {/* Navega primero al tutorial de uso, que luego lleva al quiz real */}
              <button
                onClick={() => onNavigate('tutorial')}
                className={`${styles.quizBtn} w-full rounded-xl hover:opacity-90 transition-opacity focus:outline-none focus:ring-4 focus:ring-offset-2`}
              >
                Empezar Prueba
              </button>
            </section>
          </div>

          {/* Panel lateral derecho */}
          <aside className="lg:col-span-2 space-y-8 sm:space-y-10">

            {/* Círculo de progreso */}
            <div className={`${styles.progressCard} bg-white rounded-2xl p-6 sm:p-8`}>
              <h3 className={`${styles.progressHeading} font-bold text-center mb-5`}>
                Tu Progreso
              </h3>
              <div className="flex justify-center mb-5">
                <ProgressCircle percentage={progress} nota={progress > 0 ? nota : null} />
              </div>
              <p className={`${styles.progressText} text-center`}>
                {progress > 0
                  ? <>Tu nota es <span className={styles.progressPct}>{nota}</span> de 7.0</>
                  : 'Aún no has completado el cuestionario'}
              </p>
              {tieneRespuestas && (
                <button
                  onClick={() => onNavigate('respuestas')}
                  className="mt-5 w-full rounded-xl py-3 font-bold hover:opacity-90 transition-opacity focus:outline-none focus:ring-4"
                  style={{ backgroundColor: '#1e3a5f', color: '#fff', fontSize: '1.15rem' }}
                >
                  Ver Respuestas
                </button>
              )}
            </div>

            {/* TataBot */}
            <div className={`${styles.tataBotCard} bg-white rounded-2xl p-6 sm:p-8`}>
              <TataBot />
            </div>
          </aside>
        </div>
      </main>
    </div>
  )
}
