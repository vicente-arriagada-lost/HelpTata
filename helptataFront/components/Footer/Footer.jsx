// =============================================================
// COMPONENTE Footer — Footer.jsx
// =============================================================
// Pie de página de HelpTata con información de contacto,
// descripción de la plataforma y misión.
// =============================================================
import styles from './Footer.module.scss'

export function Footer() {
  return (
    <footer className={`${styles.footer} text-white py-12 sm:py-16 mt-0`}>
      <div className="max-w-7xl mx-auto px-6 sm:px-8">
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-8 sm:gap-10">
          {/* Columna: quiénes somos */}
          <div>
            <h2 className={`${styles.colHeading} font-bold mb-4`}>¿Quiénes Somos?</h2>
            <p className={`${styles.colText} leading-relaxed`}>
              HelpTata es una plataforma dedicada a ayudar a adultos mayores a navegar el mundo digital de manera segura y confiada.
            </p>
          </div>

          {/* Columna: contacto */}
          <div>
            <h2 className={`${styles.colHeading} font-bold mb-4`}>Contacto</h2>
            <address className={`${styles.colText} not-italic leading-relaxed`}>
              <p className="mb-2">
                Email:{' '}
                <a
                  href="mailto:info@helptata.cl"
                  className="underline hover:text-white focus:outline-none focus:ring-2 focus:ring-white rounded"
                >
                  info@helptata.cl
                </a>
              </p>
              <p>
                Teléfono:{' '}
                <a
                  href="tel:+56212345678"
                  className="underline hover:text-white focus:outline-none focus:ring-2 focus:ring-white rounded"
                >
                  +56 2 1234 5678
                </a>
              </p>
            </address>
          </div>

          {/* Columna: misión */}
          <div>
            <h2 className={`${styles.colHeading} font-bold mb-4`}>Misión</h2>
            <p className={`${styles.colText} leading-relaxed`}>
              Empoderar a los adultos mayores con conocimientos digitales para una vida más conectada y segura.
            </p>
          </div>
        </div>

        {/* Línea de derechos de autor */}
        <div className={`${styles.copyright} mt-10 pt-8 border-t text-center`}>
          <p className={styles.copyrightText}>
            © 2026 HelpTata. Todos los derechos reservados.
          </p>
        </div>
      </div>
    </footer>
  )
}
