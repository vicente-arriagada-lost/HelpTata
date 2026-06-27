// =============================================================
// COMPONENTE TataBot — TataBot.jsx
// =============================================================
// Chatbot de asistencia de HelpTata conectado a la IA de Groq
// a través del microservicio msTataBot (puerto 8087).
// =============================================================
import { useState, useRef, useEffect } from 'react'
import { Send, MessageCircle } from 'lucide-react'
import { enviarMensajeTataBot } from '../../src/services/tataBotService'
import styles from './TataBot.module.scss'

function renderMensajeBot(texto) {
  const lineas = texto.split('\n')
  const bloques = []
  let i = 0

  while (i < lineas.length) {
    const linea = lineas[i].trim()
    if (!linea) { i++; continue }

    if (/^Paso\s+\d+$/i.test(linea)) {
      const instruccion = lineas[i + 1]?.trim() ?? ''
      bloques.push({ tipo: 'paso', numero: linea.replace(/\D/g, ''), texto: instruccion })
      i += 2
    } else {
      bloques.push({ tipo: 'texto', texto: linea })
      i++
    }
  }

  return bloques.map((b, idx) => {
    if (b.tipo === 'paso') {
      return (
        <div key={idx} className={`${styles.stepWrapper} flex gap-3 items-start`}>
          <span
            aria-hidden="true"
            className={`${styles.stepNumber} flex-shrink-0 flex items-center justify-center rounded-full font-bold`}
          >
            {b.numero}
          </span>
          <span style={{ paddingTop: '0.15rem' }}>{b.texto}</span>
        </div>
      )
    }
    return <p key={idx} style={{ marginBottom: '0.6rem' }}>{b.texto}</p>
  })
}

function esMensajeValido(texto) {
  const t = texto.trim()
  if (t.length < 3) return false
  if (!/[a-záéíóúüñA-ZÁÉÍÓÚÜÑ]/.test(t)) return false
  return true
}

export function TataBot() {
  const [messages, setMessages] = useState([
    { type: 'bot', text: '¡Hola! Soy TataBot, tu asistente virtual. ¿En qué puedo ayudarte hoy?' }
  ])
  const [input, setInput] = useState('')
  const [isTyping, setIsTyping] = useState(false)
  const [inputError, setInputError] = useState('')
  const messagesEndRef = useRef(null)
  const inputId = 'tatabot-input'

  useEffect(() => {
    if (messages.length <= 1 && !isTyping) return
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages, isTyping])

  const handleSend = async () => {
    if (isTyping) return

    const texto = input.trim()

    if (!esMensajeValido(texto)) {
      setInputError('Por favor escribe una pregunta antes de enviar.')
      return
    }

    setInputError('')
    setMessages(prev => [...prev, { type: 'user', text: texto }])
    setInput('')
    setIsTyping(true)

    const historial = messages
      .slice(1)
      .map(m => ({ role: m.type === 'user' ? 'user' : 'assistant', content: m.text }))

    try {
      const res = await enviarMensajeTataBot(texto, historial)
      setMessages(prev => [...prev, { type: 'bot', text: res.data.respuesta }])
    } catch {
      setMessages(prev => [...prev, {
        type: 'bot',
        text: 'Lo siento, tuve un problema al conectarme. Por favor intenta de nuevo en unos momentos.'
      }])
    } finally {
      setIsTyping(false)
    }
  }

  return (
    <div>
      <h3 className={`${styles.botHeading} font-bold mb-4 sm:mb-5 flex items-center gap-3`}>
        <MessageCircle size={32} aria-hidden="true" className={styles.botHeadingIcon} />
        TataBot — Asistente Virtual
      </h3>
      <p className={`${styles.botSubtext} mb-5 leading-relaxed`}>
        Puedes preguntarme cualquier cosa sobre el curso aquí.
      </p>

      {/* Área de mensajes con scroll */}
      <div
        role="log"
        aria-live="polite"
        aria-label="Conversación con TataBot"
        className={`${styles.messagesArea} rounded-2xl p-4 sm:p-6 mb-5 overflow-y-auto`}
      >
        {messages.map((message, index) => (
          <div
            key={index}
            className={`mb-4 ${message.type === 'user' ? 'text-right' : 'text-left'}`}
          >
            {/* Bubble colors are dynamic (user vs bot), kept inline */}
            <div
              className={`${styles.messageBubble} inline-block rounded-2xl`}
              style={{
                backgroundColor: message.type === 'user' ? '#1e3a5f' : '#ffffff',
                color: message.type === 'user' ? '#ffffff' : '#1a1a2e',
                border: message.type === 'user' ? 'none' : '2px solid #d0dae8',
              }}
            >
              <span className="sr-only">{message.type === 'user' ? 'Tú dijiste: ' : 'TataBot respondió: '}</span>
              {message.type === 'bot'
                ? renderMensajeBot(message.text)
                : <p>{message.text}</p>
              }
            </div>
          </div>
        ))}

        {isTyping && (
          <div className="text-left mb-3" aria-live="polite">
            <div className={`${styles.typingIndicatorBox} inline-block p-4 rounded-2xl`}>
              <span className="sr-only">TataBot está escribiendo…</span>
              <span aria-hidden="true" className="flex gap-2 items-center">
                <span className={`${styles.typingDot} w-4 h-4 rounded-full animate-bounce`} style={{ animationDelay: '0ms' }} />
                <span className={`${styles.typingDot} w-4 h-4 rounded-full animate-bounce`} style={{ animationDelay: '150ms' }} />
                <span className={`${styles.typingDot} w-4 h-4 rounded-full animate-bounce`} style={{ animationDelay: '300ms' }} />
              </span>
            </div>
          </div>
        )}
        <div ref={messagesEndRef} />
      </div>

      {/* Campo de texto + botón de envío */}
      <div className="flex gap-3">
        <div className="flex-1 min-w-0">
          <label htmlFor={inputId} className="sr-only">Escribe tu pregunta a TataBot</label>
          <input
            id={inputId}
            type="text"
            value={input}
            onChange={(e) => { setInput(e.target.value); setInputError('') }}
            onKeyDown={(e) => e.key === 'Enter' && handleSend()}
            placeholder="Escribe tu pregunta aquí..."
            className={`${styles.inputField} ${inputError ? styles.inputError : styles.inputNormal} w-full rounded-xl focus:outline-none focus:ring-4`}
            aria-label="Escribe tu pregunta para TataBot"
            aria-describedby={inputError ? 'tatabot-error' : undefined}
            disabled={isTyping}
          />
          {inputError && (
            <p
              id="tatabot-error"
              role="alert"
              className={styles.inputErrorMsg}
            >
              {inputError}
            </p>
          )}
        </div>
        <button
          onClick={handleSend}
          aria-label="Enviar mensaje a TataBot"
          disabled={isTyping}
          className={`${styles.sendBtn} text-white rounded-xl hover:opacity-90 transition-opacity flex items-center justify-center flex-shrink-0 focus:outline-none focus:ring-4 focus:ring-offset-2 disabled:opacity-40 disabled:cursor-not-allowed self-start`}
        >
          <Send size={28} aria-hidden="true" />
        </button>
      </div>
    </div>
  )
}
