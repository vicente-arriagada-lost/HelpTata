// =============================================================
// COMPONENTE Quiz — Quiz.jsx
// =============================================================
// Cuestionario interactivo que carga las preguntas y alternativas
// desde el backend (ms-PreguntasRespuestas, puerto 8086).
//
// Flujo de carga:
//   1. getCuestionariosPorTutorial(course.id) → obtiene el cuestionario
//   2. getPreguntasPorCuestionario(id)         → obtiene las preguntas
//   3. getAlternativasPorPregunta(id) en paralelo para cada pregunta
//
// Al completar, llama a responderCuestionario() para guardar
// las respuestas en el backend y luego invoca onComplete(courseId, score).
//
// Props:
//   course     → objeto del tutorial seleccionado { id, title }
//   user       → usuario autenticado (del AuthContext, para id_usuario)
//   onComplete → callback(courseId, score) tras finalizar
//   onNavigate → función para cambiar de página
// =============================================================
import { useState, useEffect } from 'react'
import { CheckCircle, XCircle, AlertCircle } from 'lucide-react'
import {
  getCuestionariosPorTutorial,
  getPreguntasPorCuestionario,
  getAlternativasPorPregunta,
  responderCuestionario,
} from '../../src/services/cuestionarioService'
import { getProgresoPorUsuarioYTutorial, crearProgreso, actualizarProgreso } from '../../src/services/progresoService'
import styles from './Quiz.module.scss'

export function Quiz({ course, user, onComplete, onNavigate }) {
  // Estado de carga de las preguntas
  const [preguntas, setPreguntas] = useState([])
  const [idCuestionario, setIdCuestionario] = useState(null)
  const [loadingPreguntas, setLoadingPreguntas] = useState(true)
  const [errorPreguntas, setErrorPreguntas] = useState(null)

  // Estado del quiz en progreso
  const [currentQuestion, setCurrentQuestion] = useState(0)
  const [selectedAnswer, setSelectedAnswer] = useState(null)
  // answers almacena { correcto: bool, id_pregunta, id_alternativa_seleccionada }
  const [answers, setAnswers] = useState([])
  const [showResult, setShowResult] = useState(false)
  const [noAnswerError, setNoAnswerError] = useState(false)
  // Mientras se envían respuestas al backend, bloquear la pantalla
  const [enviando, setEnviando] = useState(false)

  // Cargar cuestionario, preguntas y alternativas al montar el componente
  useEffect(() => {
    let cancelado = false

    async function cargarPreguntas() {
      try {
        // Paso 1: obtener el cuestionario del tutorial
        const resCuest = await getCuestionariosPorTutorial(course.id)
        const cuestionarios = resCuest.data
        if (!cuestionarios || cuestionarios.length === 0) {
          if (!cancelado) setErrorPreguntas('Este curso aún no tiene cuestionario disponible.')
          return
        }
        const cuestionario = cuestionarios[0]
        if (!cancelado) setIdCuestionario(cuestionario.id_cuestionario)

        // Paso 2: obtener las preguntas del cuestionario
        const resPreg = await getPreguntasPorCuestionario(cuestionario.id_cuestionario)
        const preguntasRaw = resPreg.data

        // Paso 3: obtener alternativas de cada pregunta en paralelo
        const preguntasConAlternativas = await Promise.all(
          preguntasRaw.map(async (p) => {
            const resAlt = await getAlternativasPorPregunta(p.id_pregunta)
            return {
              id: p.id_pregunta,
              question: p.enunciado_pregunta,
              options: resAlt.data.map(a => ({
                id: a.id_alternativa,
                texto: a.texto_alternativa,
                esCorrecta: a.es_correcta,
              })),
            }
          })
        )

        if (!cancelado) setPreguntas(preguntasConAlternativas)
      } catch (err) {
        if (!cancelado) setErrorPreguntas('No se pudieron cargar las preguntas. Intenta de nuevo.')
      } finally {
        if (!cancelado) setLoadingPreguntas(false)
      }
    }

    cargarPreguntas()
    return () => { cancelado = true }
  }, [course.id])

  const handleAnswerSelect = (index) => {
    setSelectedAnswer(index)
    setNoAnswerError(false)
  }

  const handleNext = async () => {
    if (selectedAnswer === null) {
      setNoAnswerError(true)
      return
    }

    const pregunta = preguntas[currentQuestion]
    const alternativaSeleccionada = pregunta.options[selectedAnswer]
    const isCorrect = alternativaSeleccionada.esCorrecta

    const newAnswers = [
      ...answers,
      {
        correcto: isCorrect,
        id_pregunta: pregunta.id,
        id_alternativa_seleccionada: alternativaSeleccionada.id,
      },
    ]
    setAnswers(newAnswers)

    if (currentQuestion < preguntas.length - 1) {
      // Avanzar a la siguiente pregunta
      setCurrentQuestion(currentQuestion + 1)
      setSelectedAnswer(null)
      setNoAnswerError(false)
    } else {
      // Último pregunta: calcular score y guardar en el backend
      const score = Math.round((newAnswers.filter(a => a.correcto).length / preguntas.length) * 100)

      if (idCuestionario && user) {
        setEnviando(true)
        try {
          // Guardar respuestas del cuestionario
          await responderCuestionario(idCuestionario, {
            id_usuario: user.id,
            respuestas: newAnswers.map(a => ({
              id_pregunta: a.id_pregunta,
              id_alternativa_seleccionada: a.id_alternativa_seleccionada,
            })),
          })
        } catch (err) {
          console.error('Error al guardar respuestas:', err)
        }

        // Guardar/actualizar progreso en msProgreso.
        // El porcentaje se calcula en el backend como (acertadas / total) * 100
        const acertadas = newAnswers.filter(a => a.correcto).length
        const falladas = newAnswers.length - acertadas
        const totalPreg = preguntas.length
        try {
          const resProgreso = await getProgresoPorUsuarioYTutorial(user.id, course.id)
          await actualizarProgreso(resProgreso.data.id_progreso, {
            recursos_completados: acertadas,
            cantidad_recursos_totales: totalPreg,
            preguntas_acertadas: acertadas,
            preguntas_falladas: falladas,
          })
        } catch {
          // No existe aún → crear registro nuevo
          try {
            await crearProgreso({
              id_usuario: user.id,
              id_tutorial: course.id,
              recursos_completados: acertadas,
              cantidad_recursos_totales: totalPreg,
              preguntas_acertadas: acertadas,
              preguntas_falladas: falladas,
            })
          } catch (err) {
            console.error('Error al guardar progreso:', err)
          }
        }

        setEnviando(false)
      }

      onComplete(course.id, score)
      setShowResult(true)
    }
  }

  // ── Pantalla de carga ──
  if (loadingPreguntas) {
    return (
      <div className={`${styles.pageWrapper} min-h-screen flex items-center justify-center`}>
        <div className={`${styles.loadingCard} bg-white rounded-2xl shadow-2xl p-10 text-center`}>
          <p className={styles.loadingText}>Cargando preguntas...</p>
        </div>
      </div>
    )
  }

  // ── Pantalla de error al cargar ──
  if (errorPreguntas) {
    return (
      <div className={`${styles.pageWrapper} min-h-screen flex items-center justify-center py-10 px-4`}>
        <div className={`${styles.loadingCard} bg-white rounded-2xl shadow-2xl p-10 text-center`}>
          <p className={`${styles.errorText} mb-6`}>{errorPreguntas}</p>
          <button onClick={() => onNavigate('course')} className={`${styles.navBtn} rounded-xl border border-blue-200 hover:bg-blue-50 focus:outline-none focus:ring-4`}>
            ← Volver al curso
          </button>
        </div>
      </div>
    )
  }

  // ── Pantalla de resultados ──
  if (showResult) {
    const score = Math.round((answers.filter(a => a.correcto).length / preguntas.length) * 100)
    const passed = score >= 70

    return (
      <div className={`${styles.pageWrapper} min-h-screen py-10 sm:py-14 px-4 sm:px-6`}>
        <main id="main-content" className="max-w-4xl mx-auto">
          <div className="bg-white rounded-2xl shadow-2xl p-8 sm:p-14 text-center">
            <h1 className={`${styles.resultHeading} font-bold mb-8`}>
              ¡Cuestionario Completado!
            </h1>

            {/* Puntuación grande con color según si aprobó — dinámico, se mantiene inline */}
            <div
              role="status"
              aria-label={`Tu puntuación es ${score} por ciento`}
              className="font-bold mb-8"
              style={{ fontSize: 'clamp(4rem, 10vw, 6rem)', color: passed ? '#1a7a45' : '#d97706' }}
            >
              {score}%
            </div>

            <p className={`${styles.resultText} mb-10 leading-relaxed`}>
              {passed
                ? '¡Excelente trabajo! Has completado el curso exitosamente.'
                : 'Buen intento. Te recomendamos revisar el material del curso.'}
            </p>

            {/* Resumen de respuestas correctas e incorrectas — colores dinámicos se mantienen inline */}
            <ul className="space-y-4 text-left mb-10" aria-label="Resumen de respuestas">
              {answers.map((a, index) => (
                <li
                  key={index}
                  className="flex items-center gap-4 p-4 sm:p-5 rounded-xl"
                  style={{ backgroundColor: a.correcto ? '#f0fdf4' : '#fef2f2', border: `2px solid ${a.correcto ? '#86efac' : '#fca5a5'}` }}
                >
                  {a.correcto
                    ? <CheckCircle size={30} aria-hidden="true" style={{ color: '#16a34a', flexShrink: 0 }} />
                    : <XCircle size={30} aria-hidden="true" style={{ color: '#dc2626', flexShrink: 0 }} />
                  }
                  <span className={styles.answerLabel}>
                    Pregunta {index + 1}: {a.correcto ? 'Correcta' : 'Incorrecta'}
                  </span>
                </li>
              ))}
            </ul>

            <button
              onClick={() => onNavigate('course')}
              className={`${styles.returnBtn} w-full rounded-xl hover:opacity-90 transition-opacity focus:outline-none focus:ring-4 focus:ring-offset-2`}
            >
              Volver al Curso
            </button>
          </div>
        </main>
      </div>
    )
  }

  // ── Pantalla de pregunta actual ──
  const pregunta = preguntas[currentQuestion]
  const progressPct = Math.round((currentQuestion / preguntas.length) * 100)

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

          {/* Barra de progreso del cuestionario */}
          <div className="mb-8">
            <div className="flex justify-between items-center mb-5">
              <span aria-live="polite" className={styles.progressLabel}>
                Pregunta {currentQuestion + 1} de {preguntas.length}
              </span>
              <span aria-hidden="true" className={styles.progressPct}>
                {progressPct}% completado
              </span>
            </div>
            <div
              role="progressbar"
              aria-valuenow={progressPct}
              aria-valuemin={0}
              aria-valuemax={100}
              aria-label={`Progreso del cuestionario: ${progressPct}%`}
              className={`${styles.progressBarTrack} w-full rounded-full`}
            >
              <div
                className={`${styles.progressBarFill} rounded-full transition-all duration-300`}
                style={{ width: `${progressPct}%` }}
              />
            </div>
          </div>

          {/* Enunciado de la pregunta */}
          <h2
            id={`question-${currentQuestion}`}
            className={`${styles.questionHeading} mb-8 leading-relaxed font-bold`}
          >
            {pregunta.question}
          </h2>

          {/* Error si avanza sin seleccionar */}
          {noAnswerError && (
            <p
              role="alert"
              className={`${styles.noAnswerError} mb-5 flex items-center gap-2 rounded-xl px-4 py-3`}
            >
              <AlertCircle size={22} aria-hidden="true" />
              Por favor selecciona una respuesta antes de continuar.
            </p>
          )}

          {/* Opciones de respuesta — estilos dinámicos basados en isSelected se mantienen inline */}
          <fieldset>
            <legend className="sr-only">Opciones de respuesta para: {pregunta.question}</legend>
            <div className="space-y-4">
              {pregunta.options.map((option, index) => {
                const isSelected = selectedAnswer === index
                return (
                  <button
                    key={option.id}
                    onClick={() => handleAnswerSelect(index)}
                    aria-pressed={isSelected}
                    className="w-full rounded-xl text-left transition-all focus:outline-none focus:ring-4 focus:ring-blue-300"
                    style={{
                      padding: '1rem 1.4rem',
                      fontSize: '1.75rem',
                      fontWeight: isSelected ? '700' : '600',
                      border: `2px solid ${isSelected ? '#1e3a5f' : '#d0dae8'}`,
                      backgroundColor: isSelected ? '#1e3a5f' : '#f9fafb',
                      color: isSelected ? '#ffffff' : '#1a1a2e',
                      transform: isSelected ? 'scale(1.02)' : 'scale(1)',
                      boxShadow: isSelected ? '0 4px 16px rgba(30,58,95,0.25)' : 'none',
                    }}
                  >
                    <span className={`${styles.optionLetter} font-bold mr-3`} aria-hidden="true">
                      {['A', 'B', 'C', 'D'][index]})
                    </span>
                    {option.texto}
                  </button>
                )
              })}
            </div>
          </fieldset>

          {/* Botón siguiente / ver resultados */}
          <button
            onClick={handleNext}
            disabled={enviando}
            className={`${styles.nextBtn} mt-10 w-full rounded-xl hover:opacity-90 transition-opacity focus:outline-none focus:ring-4 focus:ring-offset-2 disabled:opacity-60`}
          >
            {enviando
              ? 'Guardando respuestas...'
              : currentQuestion < preguntas.length - 1
                ? 'Siguiente Pregunta →'
                : 'Ver Resultados'}
          </button>
        </div>
      </main>
    </div>
  )
}
