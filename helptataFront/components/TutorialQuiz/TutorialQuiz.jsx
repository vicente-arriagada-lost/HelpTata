// =============================================================
// COMPONENTE TutorialQuiz — TutorialQuiz.jsx
// =============================================================
// Pantalla tutorial que muestra un video explicando cómo
// responder el cuestionario antes de comenzarlo.
//
// La URL del video se obtiene desde la BD via:
//   GET /api/config/url_video_tutorial (ms-Tutoriales, puerto 8082)
//
// Para cambiar el video: actualiza el registro en la tabla
// "configuracion" con clave "url_video_tutorial".
//
// Props:
//   onNavigate → función para cambiar de página
// =============================================================
import { useState, useEffect, useRef } from 'react'
import { ArrowRight, Play } from 'lucide-react'
import { getConfiguracion } from '../../src/services/tutorialService'
import styles from './TutorialQuiz.module.scss'

export function TutorialQuiz({ onNavigate }) {
  const [videoUrl, setVideoUrl] = useState(null)
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(false)
  const [mostrarOverlay, setMostrarOverlay] = useState(true)
  const videoRef = useRef(null)

  useEffect(() => {
    getConfiguracion('url_video_cuestionario')
      .then(res => setVideoUrl(res.data))
      .catch(() => setError(true))
      .finally(() => setCargando(false))
  }, [])

  const handlePlay = () => {
    setMostrarOverlay(false)
    videoRef.current?.play()
  }

  return (
    <div className={`${styles.pageWrapper} min-h-screen py-10 sm:py-14 px-4 sm:px-6`}>
      <main id="main-content" className="max-w-4xl mx-auto">
        <button
          onClick={() => onNavigate('course')}
          className={`${styles.navBtn} mb-8 hover:bg-blue-50 transition-colors focus:outline-none focus:ring-4 focus:ring-white focus:ring-offset-2 focus:ring-offset-blue-900`}
        >
          ← Volver al curso
        </button>

        <div className="bg-white rounded-2xl shadow-2xl p-8 sm:p-14">
          <h1 className={`${styles.heading} font-bold text-center mb-10`}>
            ¿Cómo responder el cuestionario?
          </h1>

          {/* Video tutorial */}
          <div className={`${styles.videoContainer} rounded-2xl overflow-hidden`}>
            {cargando && (
              <div className={`${styles.loadingBox} flex items-center justify-center`}>
                <p className={styles.loadingText}>Cargando video...</p>
              </div>
            )}

            {error && (
              <div className={`${styles.errorBox} flex items-center justify-center`}>
                <p className={styles.errorText}>
                  No se pudo cargar el video. Por favor intenta más tarde.
                </p>
              </div>
            )}

            {!cargando && !error && videoUrl && (
              <div className={styles.videoInner}>
                <video
                  ref={videoRef}
                  controls
                  width="100%"
                  className={styles.videoEl}
                  aria-label="Video tutorial: cómo responder el cuestionario"
                >
                  <source src={videoUrl} type="video/mp4" />
                  <source src={videoUrl.replace('.mp4', '.webm')} type="video/webm" />
                  Tu navegador no puede reproducir este video. Por favor actualiza tu navegador.
                </video>

                {mostrarOverlay && (
                  <div className={styles.videoOverlay}>
                    <button
                      onClick={handlePlay}
                      className={`${styles.playBtn} focus:outline-none focus:ring-4 focus:ring-white`}
                      aria-label="Reproducir video tutorial"
                    >
                      <Play size={32} aria-hidden="true" />
                      Haga click para ver
                    </button>
                  </div>
                )}
              </div>
            )}
          </div>

          {/* Instrucción debajo del video */}
          <p className={`${styles.instruction} text-center mt-8`}>
            Al acabar el video haga click en el botón de abajo para comenzar la prueba.
          </p>

          {/* Botón para iniciar el cuestionario */}
          <button
            onClick={() => onNavigate('quiz')}
            className={`${styles.startBtn} mt-8 w-full rounded-xl hover:opacity-90 transition-opacity focus:outline-none focus:ring-4 focus:ring-offset-2 flex items-center justify-center gap-3`}
          >
            ¡Entendido! Comenzar el Cuestionario
            <ArrowRight size={28} aria-hidden="true" />
          </button>
        </div>
      </main>
    </div>
  )
}
