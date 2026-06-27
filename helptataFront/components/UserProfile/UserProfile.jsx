// =============================================================
// COMPONENTE UserProfile — UserProfile.jsx
// =============================================================
// Perfil del usuario autenticado. Carga su progreso desde
// el backend (ms-Progreso, puerto 8083) y los nombres de los
// tutoriales (ms-Tutoriales, puerto 8082) para mostrar una
// vista completa del avance en cada curso.
//
// "Mis Datos" muestra: correo, teléfono, fecha de nacimiento,
// botón cambiar correo y botón cambiar contraseña.
//
// Nota de diseño: id_email == id_usuario por convención del proyecto,
// por lo que PUT /api/emails/{user.id} actualiza el correo del usuario.
//
// Props:
//   user      → usuario autenticado { id, nombre, email }
//   onNavigate → función para cambiar de página
//   onLogout   → callback para cerrar sesión
// =============================================================
import { useState, useEffect } from 'react'
import { User, Award, LogOut, Pencil, Check, X, KeyRound, Mail, CheckCircle2, Circle, Eye, EyeOff } from 'lucide-react'
import { toast } from 'react-toastify'
import { getProgresoUsuario } from '../../src/services/progresoService'
import { getTutoriales } from '../../src/services/tutorialService'
import { getUsuarioPorId, actualizarUsuario, actualizarEmail } from '../../src/services/usuarioService'
import { passwordRules, validarTelefono } from '../../src/validators/fieldValidators'
import styles from './UserProfile.module.scss'

export function UserProfile({ user, onNavigate, onLogout }) {
  const [showLogoutConfirm, setShowLogoutConfirm] = useState(false)

  // Lista de progreso cargada desde el backend
  const [progreso, setProgreso] = useState([])
  const [totalTutoriales, setTotalTutoriales] = useState(0)
  const [loadingProgreso, setLoadingProgreso] = useState(true)
  const [errorProgreso, setErrorProgreso] = useState(null)

  // Datos del usuario cargados desde la API
  const [telefono, setTelefono] = useState('')
  const [fechaNac, setFechaNac] = useState(null)
  const [datosCompletos, setDatosCompletos] = useState(null)

  // Edición de teléfono
  const [telefonoEditando, setTelefonoEditando] = useState('')
  const [editandoTelefono, setEditandoTelefono] = useState(false)
  const [guardandoTelefono, setGuardandoTelefono] = useState(false)

  // Edición de correo
  const [emailEditando, setEmailEditando] = useState('')
  const [editandoEmail, setEditandoEmail] = useState(false)
  const [guardandoEmail, setGuardandoEmail] = useState(false)

  // Cambio de contraseña
  const [editandoPassword, setEditandoPassword] = useState(false)
  const [nuevaPassword, setNuevaPassword] = useState('')
  const [nuevaPasswordTouched, setNuevaPasswordTouched] = useState(false)
  const [confirmarPassword, setConfirmarPassword] = useState('')
  const [confirmarTouched, setConfirmarTouched] = useState(false)
  const [guardandoPassword, setGuardandoPassword] = useState(false)
  const [showNuevaPassword, setShowNuevaPassword] = useState(false)
  const [showConfirmarPassword, setShowConfirmarPassword] = useState(false)

  // Carga teléfono, fecha de nacimiento y datos completos al montar
  useEffect(() => {
    if (!user?.id) return
    getUsuarioPorId(user.id)
      .then((res) => {
        setTelefono(res.data?.telefono_usuario ?? '')
        setFechaNac(res.data?.fecha_nac_usuario ?? null)
        setDatosCompletos(res.data)
      })
      .catch(() => {})
  }, [user?.id])

  // Carga progreso del usuario y nombres de los tutoriales al montar
  useEffect(() => {
    if (!user?.id) return
    let cancelado = false

    async function cargarProgreso() {
      try {
        const [resProgreso, resTutoriales] = await Promise.all([
          getProgresoUsuario(user.id),
          getTutoriales(),
        ])
        if (cancelado) return
        const nombresPorId = {}
        resTutoriales.data.forEach(t => { nombresPorId[t.id_tutor] = t.nombre_tuto })
        setTotalTutoriales(resTutoriales.data.length)
        setProgreso(resProgreso.data.map(p => ({
          idProgreso: p.id_progreso,
          idTutor: p.id_tutorial,
          nombreCurso: nombresPorId[p.id_tutorial] || `Curso ${p.id_tutorial}`,
          porcentaje: p.porcentaje_progreso ?? 0,
        })))
      } catch {
        if (!cancelado) setErrorProgreso('No se pudo cargar el progreso. Intenta más tarde.')
      } finally {
        if (!cancelado) setLoadingProgreso(false)
      }
    }

    cargarProgreso()
    return () => { cancelado = true }
  }, [user?.id])

  // Estadísticas derivadas del progreso
  const cursosCompletados = progreso.filter(p => p.porcentaje >= 70).length
  const totalCursos = totalTutoriales
  const progresoTotal = totalCursos > 0
    ? Math.round((cursosCompletados / totalCursos) * 100)
    : 0

  // Formatea YYYY-MM-DD → DD/MM/YYYY
  function formatFecha(iso) {
    if (!iso) return 'No registrada'
    const [y, m, d] = iso.split('-')
    return `${d}/${m}/${y}`
  }

  async function handleGuardarTelefono() {
    const telefonoError = validarTelefono(telefonoEditando)
    if (telefonoError) {
      toast.error(telefonoError)
      return
    }
    setGuardandoTelefono(true)
    try {
      await actualizarUsuario(user.id, { ...datosCompletos, telefono_usuario: Number(telefonoEditando) })
      setTelefono(telefonoEditando)
      setDatosCompletos(prev => ({ ...prev, telefono_usuario: Number(telefonoEditando) }))
      setEditandoTelefono(false)
      toast.success('Teléfono actualizado correctamente.')
    } catch {
      toast.error('No se pudo actualizar el teléfono. Intenta nuevamente.')
    } finally {
      setGuardandoTelefono(false)
    }
  }

  async function handleGuardarEmail() {
    if (!emailEditando.trim()) return
    setGuardandoEmail(true)
    try {
      // id_email == id_usuario por convención del proyecto
      await actualizarEmail(user.id, { email: emailEditando })
      setEditandoEmail(false)
      toast.success('Correo actualizado. Vuelve a iniciar sesión para reflejar el cambio.')
    } catch {
      toast.error('No se pudo actualizar el correo. Intenta nuevamente.')
    } finally {
      setGuardandoEmail(false)
    }
  }

  async function handleGuardarPassword() {
    if (nuevaPassword !== confirmarPassword) {
      toast.error('Las contraseñas no coinciden.')
      return
    }
    if (nuevaPassword.length < 8) {
      toast.error('La contraseña debe tener al menos 8 caracteres.')
      return
    }
    setGuardandoPassword(true)
    try {
      await actualizarUsuario(user.id, { ...datosCompletos, password_usuario: nuevaPassword })
      setEditandoPassword(false)
      setNuevaPassword('')
      setNuevaPasswordTouched(false)
      setConfirmarPassword('')
      setConfirmarTouched(false)
      setShowNuevaPassword(false)
      setShowConfirmarPassword(false)
      toast.success('Contraseña actualizada correctamente.')
    } catch {
      toast.error('No se pudo actualizar la contraseña. Intenta nuevamente.')
    } finally {
      setGuardandoPassword(false)
    }
  }

  const rowClass = 'flex flex-col sm:flex-row sm:items-center gap-4 py-4'

  return (
    <div className={`${styles.pageWrapper} min-h-screen py-10 sm:py-14 px-4 sm:px-6`}>
      <main id="main-content" className="max-w-4xl mx-auto">
        <button
          onClick={() => onNavigate('main')}
          className={`${styles.backBtn} mb-8 hover:bg-blue-50 transition-colors focus:outline-none focus:ring-4 focus:ring-white focus:ring-offset-2 focus:ring-offset-blue-900`}
        >
          ← Volver al inicio
        </button>

        <div className="bg-white rounded-2xl shadow-2xl p-8 sm:p-14">

          {/* ── Cabecera del perfil ── */}
          <div className="text-center mb-10 sm:mb-14">
            <div
              className={`${styles.avatar} inline-flex items-center justify-center rounded-full mb-6`}
              aria-hidden="true"
            >
              <User size={56} className={styles.avatarIcon} />
            </div>
            <h1 className={`${styles.userName} font-bold`}>
              {user.nombre}
            </h1>
          </div>

          {/* ── Sección de estadísticas de progreso ── */}
          <section
            aria-labelledby="progress-heading"
            className={`${styles.progressSection} rounded-2xl p-6 sm:p-8 mb-8`}
          >
            <h2 id="progress-heading" className={`${styles.sectionHeading} font-bold mb-6`}>
              Tu Progreso
            </h2>
            {loadingProgreso && (
              <p aria-live="polite" className={styles.loadingText}>Cargando progreso...</p>
            )}
            {errorProgreso && !loadingProgreso && (
              <p role="alert" className={styles.errorText}>{errorProgreso}</p>
            )}
            {!loadingProgreso && !errorProgreso && (
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
                <div className={`${styles.statCard} rounded-xl p-5 sm:p-6`}>
                  <p className={`${styles.statLabel} mb-2`}>Cursos Completados</p>
                  <p className={`${styles.statValueNavy} font-bold`} aria-label={`${cursosCompletados} de ${totalCursos} cursos completados`}>
                    {cursosCompletados} / {totalCursos}
                  </p>
                </div>
                <div className={`${styles.statCard} rounded-xl p-5 sm:p-6`}>
                  <p className={`${styles.statLabel} mb-2`}>Progreso Total</p>
                  <p className={`${styles.statValueGreen} font-bold`} aria-label={`Progreso total: ${progresoTotal} por ciento`}>
                    {progresoTotal}%
                  </p>
                </div>
              </div>
            )}
          </section>

          {/* ── Lista de cursos con barra de progreso ── */}
          {!loadingProgreso && !errorProgreso && progreso.length > 0 && (
            <section
              aria-labelledby="courses-heading"
              className={`${styles.courseListSection} rounded-2xl p-6 sm:p-8 mb-8`}
            >
              <h2 id="courses-heading" className={`${styles.sectionHeading} font-bold mb-6`}>
                Cursos y Calificaciones
              </h2>
              <ul className="space-y-4" aria-label="Lista de cursos y su progreso">
                {progreso.map(p => {
                  const passed = p.porcentaje >= 70
                  return (
                    <li key={p.idProgreso} className={`${styles.courseItem} bg-white rounded-xl p-5 sm:p-6 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4`}>
                      <div className="flex-1">
                        <h3 className={`${styles.courseName} mb-3`}>{p.nombreCurso}</h3>
                        <div role="progressbar" aria-valuenow={p.porcentaje} aria-valuemin={0} aria-valuemax={100} aria-label={`Progreso de ${p.nombreCurso}: ${p.porcentaje}%`} className={`${styles.progressBarTrack} w-full rounded-full`}>
                          {/* Dynamic color based on passed — kept inline */}
                          <div className="rounded-full transition-all duration-500" style={{ width: `${p.porcentaje}%`, height: '12px', backgroundColor: passed ? '#16a34a' : '#1e3a5f' }} />
                        </div>
                      </div>
                      <div className="flex items-center gap-3 sm:ml-6">
                        <span className={`${styles.coursePct} font-bold`} aria-hidden="true">{p.porcentaje}%</span>
                        {passed && <Award size={30} aria-label="Curso aprobado" style={{ color: '#16a34a' }} />}
                      </div>
                    </li>
                  )
                })}
              </ul>
            </section>
          )}

          {/* ── Mis Datos ── */}
          <section
            aria-labelledby="edit-heading"
            className={`${styles.dataSection} rounded-2xl p-6 sm:p-8 mb-8`}
          >
            <h2 id="edit-heading" className={`${styles.sectionHeading} font-bold mb-2`}>
              Mis Datos
            </h2>

            {/* ── Correo ── */}
            <div className={rowClass}>
              <span className={styles.dataLabel}>Correo:</span>
              {editandoEmail ? (
                <div className="flex items-center gap-3 flex-1">
                  <input
                    type="email"
                    value={emailEditando}
                    onChange={(e) => setEmailEditando(e.target.value)}
                    placeholder="nuevo@correo.com"
                    className={`${styles.editInput} flex-1 focus:outline-none focus:ring-4 focus:ring-blue-300`}
                    autoFocus
                  />
                  <button onClick={handleGuardarEmail} disabled={guardandoEmail} aria-label="Guardar correo"
                    className={`${styles.btnSave} hover:opacity-90 focus:outline-none focus:ring-4 focus:ring-green-300`}
                    style={{ opacity: guardandoEmail ? 0.6 : 1, cursor: guardandoEmail ? 'not-allowed' : 'pointer' }}>
                    <Check size={22} aria-hidden="true" />
                  </button>
                  <button onClick={() => setEditandoEmail(false)} aria-label="Cancelar"
                    className={`${styles.btnCancel} hover:opacity-90 focus:outline-none focus:ring-4 focus:ring-gray-300`}>
                    <X size={22} aria-hidden="true" />
                  </button>
                </div>
              ) : (
                <div className="flex items-center gap-4 flex-1 flex-wrap">
                  <span className={styles.dataValue}>{user.email}</span>
                  <button
                    onClick={() => { setEmailEditando(user.email); setEditandoEmail(true) }}
                    aria-label="Cambiar correo"
                    className={`${styles.btnChangeEmail} flex items-center gap-2 rounded-xl hover:opacity-80 transition-opacity focus:outline-none focus:ring-4 focus:ring-blue-300`}
                  >
                    <Mail size={18} aria-hidden="true" />
                    Cambiar correo
                  </button>
                </div>
              )}
            </div>

            <div className={styles.divider} />

            {/* ── Teléfono ── */}
            <div className={rowClass}>
              <label htmlFor="telefono-field" className={styles.dataLabel}>Teléfono:</label>
              {editandoTelefono ? (
                <div className="flex items-center gap-3 flex-1">
                  <input
                    id="telefono-field"
                    type="tel"
                    value={telefonoEditando}
                    onChange={(e) => {
                      const v = e.target.value
                      const hasPlus = v.startsWith('+')
                      setTelefonoEditando((hasPlus ? '+' : '') + v.replace(/[^0-9]/g, ''))
                    }}
                    placeholder="+56912345678"
                    maxLength={12}
                    className={`${styles.editInput} flex-1 focus:outline-none focus:ring-4 focus:ring-blue-300`}
                    autoFocus
                  />
                  <button onClick={handleGuardarTelefono} disabled={guardandoTelefono} aria-label="Guardar teléfono"
                    className={`${styles.btnSave} hover:opacity-90 focus:outline-none focus:ring-4 focus:ring-green-300`}
                    style={{ opacity: guardandoTelefono ? 0.6 : 1, cursor: guardandoTelefono ? 'not-allowed' : 'pointer' }}>
                    <Check size={22} aria-hidden="true" />
                  </button>
                  <button onClick={() => setEditandoTelefono(false)} aria-label="Cancelar"
                    className={`${styles.btnCancel} hover:opacity-90 focus:outline-none focus:ring-4 focus:ring-gray-300`}>
                    <X size={22} aria-hidden="true" />
                  </button>
                </div>
              ) : (
                <div className="flex items-center gap-4 flex-1">
                  <span className={styles.dataValue}>
                    {telefono || <em className={styles.dataMuted}>No registrado</em>}
                  </span>
                  <button
                    onClick={() => { setTelefonoEditando(String(telefono)); setEditandoTelefono(true) }}
                    aria-label="Editar teléfono"
                    className={`${styles.btnEditPhone} rounded-xl hover:opacity-80 transition-opacity focus:outline-none focus:ring-4 focus:ring-blue-300`}
                  >
                    <Pencil size={20} aria-hidden="true" />
                  </button>
                </div>
              )}
            </div>

            <div className={styles.divider} />

            {/* ── Fecha de nacimiento ── */}
            <div className={rowClass}>
              <span className={styles.dataLabel}>Fecha de nacimiento:</span>
              <span className={styles.dataValue}>{formatFecha(fechaNac)}</span>
            </div>

            <div className={styles.divider} />

            {/* ── Cambiar contraseña ── */}
            {editandoPassword ? (
              <div className="pt-4 flex flex-col gap-4">
                <p className={`${styles.passwordHeading} font-semibold`}>Nueva contraseña</p>
                <div className={styles.passwordWrapper}>
                  <input
                    type={showNuevaPassword ? 'text' : 'password'}
                    value={nuevaPassword}
                    onChange={(e) => { setNuevaPassword(e.target.value); setNuevaPasswordTouched(true) }}
                    placeholder="Mínimo 8 caracteres"
                    className={`${styles.editInput} ${styles.passwordInput} w-full focus:outline-none focus:ring-4 focus:ring-blue-300`}
                    style={{ border: `2px solid ${nuevaPasswordTouched && nuevaPassword && !passwordRules.every(r => r.test(nuevaPassword)) ? '#dc2626' : '#c0d4ec'}` }}
                    autoFocus
                  />
                  <button type="button" onClick={() => setShowNuevaPassword(v => !v)}
                    className={styles.toggleBtn} aria-label={showNuevaPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'} tabIndex={-1}>
                    {showNuevaPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                  </button>
                </div>
                {nuevaPasswordTouched && nuevaPassword && !passwordRules.every(r => r.test(nuevaPassword)) && (
                  <p role="alert" className="flex items-center gap-2 mt-1" style={{ color: '#dc2626', fontSize: '1.25rem' }}>
                    <X size={16} aria-hidden="true" />
                    La contraseña no cumple todos los requisitos.
                  </p>
                )}
                <ul className="space-y-2 mt-1" aria-label="Requisitos de contraseña">
                  {passwordRules.map(rule => {
                    const met = nuevaPassword.length > 0 && rule.test(nuevaPassword)
                    return (
                      <li key={rule.key} className="flex items-center gap-2"
                        style={{ fontSize: '1.25rem', color: met ? '#15803d' : '#6b7280', fontWeight: met ? '600' : '400', transition: 'color 0.2s' }}
                        aria-label={`${rule.label}: ${met ? 'cumplido' : 'pendiente'}`}>
                        {met
                          ? <CheckCircle2 size={20} style={{ color: '#15803d', flexShrink: 0 }} aria-hidden="true" />
                          : <Circle size={20} style={{ color: '#d1d5db', flexShrink: 0 }} aria-hidden="true" />}
                        {rule.label}
                      </li>
                    )
                  })}
                </ul>
                <div className={styles.passwordWrapper}>
                  <input
                    type={showConfirmarPassword ? 'text' : 'password'}
                    value={confirmarPassword}
                    onChange={(e) => { setConfirmarPassword(e.target.value); setConfirmarTouched(true) }}
                    placeholder="Confirmar nueva contraseña"
                    className={`${styles.editInput} ${styles.passwordInput} w-full focus:outline-none focus:ring-4 focus:ring-blue-300`}
                    style={{ border: `2px solid ${confirmarTouched && confirmarPassword && confirmarPassword !== nuevaPassword ? '#dc2626' : '#c0d4ec'}` }}
                  />
                  <button type="button" onClick={() => setShowConfirmarPassword(v => !v)}
                    className={styles.toggleBtn} aria-label={showConfirmarPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'} tabIndex={-1}>
                    {showConfirmarPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                  </button>
                </div>
                {confirmarTouched && confirmarPassword && confirmarPassword !== nuevaPassword && (
                  <p role="alert" className="flex items-center gap-2 mt-1" style={{ color: '#dc2626', fontSize: '1.25rem' }}>
                    <X size={16} aria-hidden="true" />
                    Las contraseñas no coinciden.
                  </p>
                )}
                <div className="flex gap-3">
                  <button
                    onClick={handleGuardarPassword}
                    disabled={guardandoPassword}
                    className={`${styles.passwordSaveBtn} flex-1 rounded-xl hover:opacity-90 transition-opacity focus:outline-none focus:ring-4`}
                    style={{ cursor: guardandoPassword ? 'not-allowed' : 'pointer', opacity: guardandoPassword ? 0.7 : 1 }}
                  >
                    {guardandoPassword ? 'Guardando...' : 'Guardar contraseña'}
                  </button>
                  <button
                    onClick={() => { setEditandoPassword(false); setNuevaPassword(''); setNuevaPasswordTouched(false); setConfirmarPassword(''); setConfirmarTouched(false); setShowNuevaPassword(false); setShowConfirmarPassword(false) }}
                    className={`${styles.passwordCancelBtn} flex-1 rounded-xl hover:opacity-90 transition-opacity focus:outline-none focus:ring-4`}
                  >
                    Cancelar
                  </button>
                </div>
              </div>
            ) : (
              <div className="pt-4">
                <button
                  onClick={() => setEditandoPassword(true)}
                  className={`${styles.btnChangePassword} flex items-center gap-3 rounded-xl hover:opacity-90 transition-opacity focus:outline-none focus:ring-4 focus:ring-blue-300`}
                >
                  <KeyRound size={22} aria-hidden="true" />
                  Cambiar contraseña
                </button>
              </div>
            )}
          </section>

          {/* ── Botón / confirmación de cierre de sesión ── */}
          {!showLogoutConfirm ? (
            <button
              onClick={() => setShowLogoutConfirm(true)}
              className={`${styles.logoutBtn} w-full rounded-xl hover:opacity-90 transition-opacity focus:outline-none focus:ring-4 focus:ring-offset-2 flex items-center justify-center gap-3`}
            >
              <LogOut size={26} aria-hidden="true" />
              Cerrar Sesión
            </button>
          ) : (
            <div
              role="alertdialog"
              aria-labelledby="logout-dialog-title"
              aria-describedby="logout-dialog-desc"
              className={`${styles.logoutDialog} rounded-2xl p-6 sm:p-8`}
            >
              <h3 id="logout-dialog-title" className={`${styles.logoutDialogTitle} font-bold mb-3`}>
                ¿Estás seguro que deseas cerrar sesión?
              </h3>
              <p id="logout-dialog-desc" className={`${styles.logoutDialogDesc} mb-7`}>
                Tu progreso quedará guardado y podrás volver a ingresar cuando quieras.
              </p>
              <div className="flex gap-4 flex-col sm:flex-row">
                <button
                  onClick={onLogout}
                  className={`${styles.logoutConfirmBtn} flex-1 rounded-xl hover:opacity-90 transition-opacity focus:outline-none focus:ring-4 focus:ring-offset-2`}
                >
                  Sí, cerrar sesión
                </button>
                <button
                  onClick={() => setShowLogoutConfirm(false)}
                  autoFocus
                  className={`${styles.logoutCancelBtn} flex-1 rounded-xl hover:opacity-90 transition-colors focus:outline-none focus:ring-4 focus:ring-offset-2`}
                >
                  Cancelar
                </button>
              </div>
            </div>
          )}
        </div>
      </main>
    </div>
  )
}
