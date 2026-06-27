// =============================================================
// COMPONENTE CourseCard — CourseCard.jsx
// =============================================================
// Tarjeta que representa un curso en la grilla de la MainPage.
// Muestra imagen de portada (con fallback si no carga), título,
// descripción corta y botón para entrar al curso.
//
// Props:
//   course   → objeto con { id, title, shortDescription, image, fullDescription, videoUrl }
//   onSelect → función que recibe el course completo y navega al CoursePage
// =============================================================
import { useState } from 'react'
import styles from './CourseCard.module.scss'

/**
 * Imagen con fallback automático si la URL falla.
 * Muestra un placeholder SVG cuando la imagen no carga.
 */
function ImageWithFallback({ src, alt, style, className, ...rest }) {
  const [didError, setDidError] = useState(false)

  // Si la imagen dio error, mostrar el placeholder SVG
  if (didError) {
    return (
      <div
        className={`inline-block bg-gray-100 text-center align-middle ${className ?? ''}`}
        style={style}
      >
        <div className="flex items-center justify-center w-full h-full">
          <img
            src="data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iODgiIGhlaWdodD0iODgiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgc3Ryb2tlPSIjMDAwIiBzdHJva2UtbGluZWpvaW49InJvdW5kIiBvcGFjaXR5PSIuMyIgZmlsbD0ibm9uZSIgc3Ryb2tlLXdpZHRoPSIzLjciPjxyZWN0IHg9IjE2IiB5PSIxNiIgd2lkdGg9IjU2IiBoZWlnaHQ9IjU2IiByeD0iNiIvPjxwYXRoIGQ9Im0xNiA1OCAxNi0xOCAzMiAzMiIvPjxjaXJjbGUgY3g9IjUzIiBjeT0iMzUiIHI9IjciLz48L3N2Zz4KCg=="
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
      {...rest}
      onError={() => setDidError(true)}
    />
  )
}

export function CourseCard({ course, onSelect }) {
  return (
    <article
      className={`${styles.card} bg-white rounded-2xl overflow-hidden flex flex-col h-full transition-shadow hover:shadow-2xl`}
    >
      {/* Imagen de portada del curso */}
      <ImageWithFallback
        src={course.image}
        alt={`Imagen del curso: ${course.title}`}
        className={`${styles.coverImage} w-full object-cover`}
      />

      <div className="p-6 sm:p-8 flex flex-col flex-grow">
        {/* Título del curso */}
        <h3 className={`${styles.title} font-bold mb-4`}>
          {course.title}
        </h3>

        {/* Descripción corta */}
        <p className={`${styles.description} leading-relaxed mb-7 flex-grow`}>
          {course.shortDescription}
        </p>

        {/* Botón para entrar al curso */}
        <button
          onClick={() => onSelect(course)}
          aria-label={`Ver curso: ${course.title}`}
          className={`${styles.btn} w-full text-white rounded-xl hover:opacity-90 transition-opacity focus:outline-none focus:ring-4 focus:ring-offset-2`}
        >
          Ver Curso →
        </button>
      </div>
    </article>
  )
}
