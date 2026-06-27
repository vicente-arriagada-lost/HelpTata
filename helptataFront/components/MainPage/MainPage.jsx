// =============================================================
// COMPONENTE MainPage — MainPage.jsx
// =============================================================
// Página principal de HelpTata. Carga los tutoriales y sus
// fotos desde el backend (ms-Tutoriales, puerto 8082) y los
// muestra dinámicamente. También incluye la sección hero, el
// video introductorio y el chatbot TataBot.
//
// Props:
//   user           → usuario autenticado (puede ser null si no hay sesión)
//   onNavigate     → función para cambiar de página
//   onSelectCourse → callback que recibe el curso seleccionado
// =============================================================
import { useState, useEffect, useRef } from 'react'
import Plyr from 'plyr'
import 'plyr/dist/plyr.css'

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
import { Header } from '../Header/Header'
import { Footer } from '../Footer/Footer'
import { CourseCard } from '../CourseCard/CourseCard'
import { TataBot } from '../TataBot/TataBot'
import { getTutoriales, getTodasLasFotos, getConfiguracion } from '../../src/services/tutorialService'
import styles from './MainPage.module.scss'

// Componente auxiliar: muestra una imagen con fallback SVG si falla la carga
function ImagenConFallback({ src, alt, style, className, ...rest }) {
  const [error, setError] = useState(false)

  if (error) {
    return (
      <div
        className={`inline-block bg-gray-100 text-center align-middle ${className ?? ''}`}
        style={style}
      >
        <div className="flex items-center justify-center w-full h-full">
          {/* SVG base64 de "imagen rota" como placeholder */}
          <img
            src="data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iODgiIGhlaWdodD0iODgiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgc3Ryb2tlPSIjMDAwIiBzdHJva2UtbGluZWpvaW49InJvdW5kIiBvcGFjaXR5PSIuMyIgZmlsbD0ibm9uZSIgc3Ryb2tlLXdpZHRoPSIzLjciPjxyZWN0IHg9IjE2IiB5PSIxNiIgd2lkdGg9IjU2IiBoZWlnaHQ9IjU2IiByeD0iNiIvPjxwYXRoIGQ9Im0xNiA1OCAxNi0xOCAzMiAzMiIvPjxjaXJjbGUgY3g9IjUzIiBjeT0iMzUiIHI9IjciLz48L3N2Zz4K"
            alt="Imagen no disponible"
            data-original-url={src}
          />
        </div>
      </div>
    )
  }

  return (
    <img
      src={src}
      alt={alt}
      className={className}
      style={style}
      onError={() => setError(true)}
      {...rest}
    />
  )
}

export function MainPage({ user, onNavigate, onSelectCourse }) {
  const [videoIntroUrl, setVideoIntroUrl] = useState(null)
  const videoIntroRef = useRef(null)
  const plyrIntroRef = useRef(null)
  const [mostrarOverlay, setMostrarOverlay] = useState(true)

  const handlePlayIntro = () => {
    setMostrarOverlay(false)
    plyrIntroRef.current?.play()
  }

  // Estado de los cursos cargados desde la API
  const [courses, setCourses] = useState([])
  const [loadingCourses, setLoadingCourses] = useState(true)
  // errorCursos se muestra si la petición al backend falla
  const [errorCursos, setErrorCursos] = useState(null)

  // Carga la URL del video introductorio desde la base de datos
  useEffect(() => {
    getConfiguracion('url_video_tutorial')
      .then(res => setVideoIntroUrl(res.data))
      .catch(() => setVideoIntroUrl('http://localhost:9000/VideoIntroductorio.mp4'))
  }, [])

  // Inicializa Plyr al montar (el elemento video siempre está en el DOM)
  useEffect(() => {
    if (!videoIntroRef.current) return
    plyrIntroRef.current = new Plyr(videoIntroRef.current, PLYR_OPTIONS)
    return () => { plyrIntroRef.current?.destroy(); plyrIntroRef.current = null }
  }, [])

  // Carga tutoriales y fotos al montar el componente
  useEffect(() => {
    let cancelado = false // evita actualizar estado si el componente se desmontó

    async function cargarCursos() {
      try {
        // Petición paralela: tutoriales + fotos
        const [resTutoriales, resFotos] = await Promise.all([
          getTutoriales(),
          getTodasLasFotos(),
        ])

        if (cancelado) return

        const tutoriales = resTutoriales.data
        const fotos = resFotos.data

        // Mapear cada tutorial al formato esperado por CourseCard
        const cursosFormateados = tutoriales.map(t => {
          // Buscar la primera foto asociada al tutorial por id_tutor
          const foto = fotos.find(f => f.id_tutor === t.id_tutor)
          return {
            id: t.id_tutor,
            title: t.nombre_tuto,
            shortDescription: t.descripcion_tuto,
            fullDescription: t.descripcion_tuto,
            image: foto?.foto || null,
            videoUrl: t.tutorial,
          }
        })

        setCourses(cursosFormateados)
      } catch (err) {
        if (!cancelado) {
          setErrorCursos('No se pudieron cargar los cursos. Intenta recargar la página.')
        }
      } finally {
        if (!cancelado) setLoadingCourses(false)
      }
    }

    cargarCursos()
    return () => { cancelado = true }
  }, [])

  return (
    <div className={`${styles.pageWrapper} min-h-screen`}>
      <Header user={user} onNavigate={onNavigate} showCourseButton={true} />

      <main id="main-content">

        {/* ── HERO ── */}
        <section
          aria-labelledby="hero-heading"
          className={`${styles.heroSection} py-16 sm:py-20 md:py-24`}
        >
          <div className="max-w-4xl mx-auto px-6 sm:px-8 text-center">
            <h1
              id="hero-heading"
              className={`${styles.heroHeading} text-white font-bold mb-6`}
            >
              Aprende a tu propio ritmo
            </h1>
            <p className={`${styles.heroSubtext} text-white leading-relaxed mb-10 mx-auto`}>
              HelpTata es tu compañero en el mundo digital. Ofrecemos cursos diseñados
              especialmente para adultos mayores, con lecciones claras y fáciles de seguir.
              Aprende a usar tecnología, protégete de estafas y mantente conectado con tus
              seres queridos.
            </p>
            <button
              onClick={() => {
                // Desplazar suavemente hacia la sección de cursos
                document.getElementById('cursos')?.scrollIntoView({ behavior: 'smooth' })
              }}
              className={`${styles.heroBtn} rounded-xl hover:opacity-90 transition-opacity focus:outline-none focus:ring-4 focus:ring-white focus:ring-offset-2`}
            >
              ▶ Ver Cursos Disponibles
            </button>
          </div>
        </section>

        {/* ── VIDEO INTRO ── */}
        <section
          aria-labelledby="video-intro-heading"
          className={`${styles.videoSection} py-14 sm:py-18`}
        >
          <div className="max-w-5xl mx-auto px-6 sm:px-8">
            <h2
              id="video-intro-heading"
              className={`${styles.sectionHeading} font-bold text-center mb-8`}
            >
              Conoce HelpTata
            </h2>
            <div className={`${styles.videoWrapper} rounded-2xl overflow-hidden shadow-2xl`}>
              <video ref={videoIntroRef} playsInline className={styles.videoEl}>
                {videoIntroUrl && <source src={videoIntroUrl} type="video/mp4" />}
              </video>
              {mostrarOverlay && videoIntroUrl && (
                <div className={styles.videoOverlay}>
                  <button
                    onClick={handlePlayIntro}
                    className={`${styles.videoPlayBtn} focus:outline-none focus:ring-4 focus:ring-white`}
                    aria-label="Reproducir video introductorio"
                  >
                    ▶ Haga click para ver
                  </button>
                </div>
              )}
            </div>
          </div>
        </section>

        {/* ── CURSOS (dinámicos desde el backend) ── */}
        <section
          id="cursos"
          aria-labelledby="courses-heading"
          className={`${styles.coursesSection} py-14 sm:py-18`}
        >
          <div className="max-w-7xl mx-auto px-6 sm:px-8">
            <h2
              id="courses-heading"
              className={`${styles.sectionHeading} font-bold text-center mb-10 sm:mb-14`}
            >
              Nuestros Cursos
            </h2>

            {/* Estado de carga */}
            {loadingCourses && (
              <div className="text-center py-16" aria-live="polite" aria-busy="true">
                <p className={styles.loadingText}>Cargando cursos...</p>
              </div>
            )}

            {/* Error al cargar */}
            {errorCursos && !loadingCourses && (
              <div
                role="alert"
                className={`${styles.errorBox} rounded-2xl p-8 text-center mx-auto`}
              >
                <p className={styles.errorText}>{errorCursos}</p>
              </div>
            )}

            {/* Grilla de CourseCards */}
            {!loadingCourses && !errorCursos && (
              courses.length === 0 ? (
                <p className={`${styles.emptyText} text-center`}>
                  No hay cursos disponibles en este momento.
                </p>
              ) : (
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8 sm:gap-10">
                  {courses.map(course => (
                    <CourseCard
                      key={course.id}
                      course={course}
                      onSelect={onSelectCourse}
                    />
                  ))}
                </div>
              )
            )}
          </div>
        </section>

        {/* ── TATABOT ── */}
        <section
          aria-labelledby="tatabot-heading"
          className={`${styles.tataBotSection} py-14 sm:py-18`}
        >
          <div className="max-w-5xl mx-auto px-6 sm:px-8">
            <h2
              id="tatabot-heading"
              className={`${styles.sectionHeading} font-bold text-center mb-8`}
            >
              Pregúntale a TataBot
            </h2>
            <div className={`${styles.tataBotCard} bg-white rounded-2xl p-6 sm:p-10`}>
              <TataBot />
            </div>
          </div>
        </section>

      </main>

      <Footer />
    </div>
  )
}
