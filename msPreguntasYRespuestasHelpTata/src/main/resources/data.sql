-- ============================================================
-- ms-PreguntasRespuestas: datos de prueba
-- 3 cuestionarios (uno por tutorial), 4-5 preguntas cada uno
-- ============================================================

-- CUESTIONARIOS
INSERT INTO cuestionarios (id_cuestionario, titulo_cuestionario, descripcion_cuestionario, id_tutor)
VALUES
  (1, 'Cuestionario: Estafas Digitales',  'Evalúa tu conocimiento sobre seguridad en internet', 1),
  (2, 'Cuestionario: Uso de Smartphone',  'Pon a prueba lo que aprendiste sobre tu teléfono',   2),
  (3, 'Cuestionario: Internet Seguro',    'Demuestra que navegas de forma segura',               3)
ON CONFLICT (id_cuestionario) DO NOTHING;

-- PREGUNTAS — Cuestionario 1 (Estafas Digitales)
INSERT INTO preguntas (id_pregunta, enunciado_pregunta, id_cuestionario)
VALUES
  (1,  '¿Cuál es una señal de un correo electrónico fraudulento?',       1),
  (2,  '¿Qué debes hacer si recibes un mensaje sospechoso?',             1),
  (3,  '¿Cómo debe ser una contraseña segura?',                          1),
  (4,  '¿Es seguro compartir tu contraseña con amigos?',                 1),
  (5,  '¿Qué significa HTTPS en una dirección web?',                     1)
ON CONFLICT (id_pregunta) DO NOTHING;

-- PREGUNTAS — Cuestionario 2 (Uso de Smartphone)
INSERT INTO preguntas (id_pregunta, enunciado_pregunta, id_cuestionario)
VALUES
  (6,  '¿Qué es una aplicación móvil?',                                  2),
  (7,  '¿Cómo puedes cuidar la batería de tu smartphone?',               2),
  (8,  '¿Qué debes revisar antes de instalar una aplicación?',           2),
  (9,  '¿Para qué sirve el modo avión?',                                 2)
ON CONFLICT (id_pregunta) DO NOTHING;

-- PREGUNTAS — Cuestionario 3 (Internet Seguro)
INSERT INTO preguntas (id_pregunta, enunciado_pregunta, id_cuestionario)
VALUES
  (10, '¿Qué indica el candado en la barra de dirección del navegador?', 3),
  (11, '¿Qué es el spam?',                                               3),
  (12, '¿Cómo proteger tu información en redes sociales?',               3),
  (13, '¿Qué debes hacer al terminar de usar un computador público?',    3)
ON CONFLICT (id_pregunta) DO NOTHING;

-- ALTERNATIVAS — Pregunta 1
INSERT INTO alternativas (id_alternativa, texto_alternativa, es_correcta, id_pregunta)
VALUES
  (1,  'El correo tiene errores de ortografía',                   false, 1),
  (2,  'Solicita información personal urgente',                   false, 1),
  (3,  'El remitente es desconocido',                             false, 1),
  (4,  'Todas las anteriores',                                    true,  1)
ON CONFLICT (id_alternativa) DO NOTHING;

-- ALTERNATIVAS — Pregunta 2
INSERT INTO alternativas (id_alternativa, texto_alternativa, es_correcta, id_pregunta)
VALUES
  (5,  'Hacer clic en los enlaces de inmediato',                  false, 2),
  (6,  'Responder con tu información personal',                   false, 2),
  (7,  'Eliminarlo y reportarlo como spam',                       true,  2),
  (8,  'Reenviar a todos tus contactos',                          false, 2)
ON CONFLICT (id_alternativa) DO NOTHING;

-- ALTERNATIVAS — Pregunta 3
INSERT INTO alternativas (id_alternativa, texto_alternativa, es_correcta, id_pregunta)
VALUES
  (9,  'Tu fecha de nacimiento',                                  false, 3),
  (10, 'Una combinación de letras, números y símbolos',           true,  3),
  (11, 'Tu nombre completo',                                      false, 3),
  (12, 'La palabra contraseña',                                   false, 3)
ON CONFLICT (id_alternativa) DO NOTHING;

-- ALTERNATIVAS — Pregunta 4
INSERT INTO alternativas (id_alternativa, texto_alternativa, es_correcta, id_pregunta)
VALUES
  (13, 'Sí, si son amigos de confianza',                          false, 4),
  (14, 'Sí, pero solo por correo electrónico',                    false, 4),
  (15, 'No, nunca debes compartir tus contraseñas',               true,  4),
  (16, 'Sí, si son familiares directos',                          false, 4)
ON CONFLICT (id_alternativa) DO NOTHING;

-- ALTERNATIVAS — Pregunta 5
INSERT INTO alternativas (id_alternativa, texto_alternativa, es_correcta, id_pregunta)
VALUES
  (17, 'Es un sitio web muy rápido',                              false, 5),
  (18, 'Es una conexión segura y encriptada',                     true,  5),
  (19, 'Es un sitio web gratuito',                                false, 5),
  (20, 'No significa nada importante',                            false, 5)
ON CONFLICT (id_alternativa) DO NOTHING;

-- ALTERNATIVAS — Pregunta 6
INSERT INTO alternativas (id_alternativa, texto_alternativa, es_correcta, id_pregunta)
VALUES
  (21, 'Un programa de televisión',                               false, 6),
  (22, 'Un programa que se instala en el smartphone',             true,  6),
  (23, 'Una herramienta física del teléfono',                     false, 6),
  (24, 'Un tipo de cargador',                                     false, 6)
ON CONFLICT (id_alternativa) DO NOTHING;

-- ALTERNATIVAS — Pregunta 7
INSERT INTO alternativas (id_alternativa, texto_alternativa, es_correcta, id_pregunta)
VALUES
  (25, 'Dejar el brillo al máximo siempre',                       false, 7),
  (26, 'Cerrar las aplicaciones que no usas',                     true,  7),
  (27, 'Mantener el Bluetooth activado siempre',                  false, 7),
  (28, 'Descargar muchas aplicaciones',                           false, 7)
ON CONFLICT (id_alternativa) DO NOTHING;

-- ALTERNATIVAS — Pregunta 8
INSERT INTO alternativas (id_alternativa, texto_alternativa, es_correcta, id_pregunta)
VALUES
  (29, 'Instalar sin leer nada',                                  false, 8),
  (30, 'Revisar los permisos que solicita la aplicación',         true,  8),
  (31, 'Solo mirar cuántas estrellas tiene',                      false, 8),
  (32, 'Preguntar a un desconocido',                              false, 8)
ON CONFLICT (id_alternativa) DO NOTHING;

-- ALTERNATIVAS — Pregunta 9
INSERT INTO alternativas (id_alternativa, texto_alternativa, es_correcta, id_pregunta)
VALUES
  (33, 'Para tomar fotos más rápido',                             false, 9),
  (34, 'Para desactivar todas las conexiones inalámbricas',       true,  9),
  (35, 'Para cargar más rápido la batería',                       false, 9),
  (36, 'Para escuchar música sin internet',                       false, 9)
ON CONFLICT (id_alternativa) DO NOTHING;

-- ALTERNATIVAS — Pregunta 10
INSERT INTO alternativas (id_alternativa, texto_alternativa, es_correcta, id_pregunta)
VALUES
  (37, 'Que el sitio es gratuito',                                false, 10),
  (38, 'Que la conexión es segura y encriptada',                  true,  10),
  (39, 'Que el sitio carga lento',                                false, 10),
  (40, 'Que el sitio tiene muchas imágenes',                      false, 10)
ON CONFLICT (id_alternativa) DO NOTHING;

-- ALTERNATIVAS — Pregunta 11
INSERT INTO alternativas (id_alternativa, texto_alternativa, es_correcta, id_pregunta)
VALUES
  (41, 'Mensajes importantes de tu banco',                        false, 11),
  (42, 'Correo basura o no deseado',                              true,  11),
  (43, 'Mensajes de tus amigos',                                  false, 11),
  (44, 'Un tipo de virus informático',                            false, 11)
ON CONFLICT (id_alternativa) DO NOTHING;

-- ALTERNATIVAS — Pregunta 12
INSERT INTO alternativas (id_alternativa, texto_alternativa, es_correcta, id_pregunta)
VALUES
  (45, 'Publicar toda tu información personal',                   false, 12),
  (46, 'Revisar y ajustar la configuración de privacidad',        true,  12),
  (47, 'Aceptar solicitudes de amistad de todos',                 false, 12),
  (48, 'Compartir tu contraseña con amigos',                      false, 12)
ON CONFLICT (id_alternativa) DO NOTHING;

-- ALTERNATIVAS — Pregunta 13
INSERT INTO alternativas (id_alternativa, texto_alternativa, es_correcta, id_pregunta)
VALUES
  (49, 'Dejar la sesión abierta para el próximo usuario',         false, 13),
  (50, 'Cerrar sesión y borrar el historial de navegación',       true,  13),
  (51, 'Solo apagar el monitor',                                  false, 13),
  (52, 'No hacer nada, el computador lo hace solo',               false, 13)
ON CONFLICT (id_alternativa) DO NOTHING;

-- Resetear secuencias
SELECT setval(pg_get_serial_sequence('cuestionarios', 'id_cuestionario'), COALESCE((SELECT MAX(id_cuestionario) FROM cuestionarios), 1));
SELECT setval(pg_get_serial_sequence('preguntas',     'id_pregunta'),     COALESCE((SELECT MAX(id_pregunta)     FROM preguntas),     1));
SELECT setval(pg_get_serial_sequence('alternativas',  'id_alternativa'),  COALESCE((SELECT MAX(id_alternativa)  FROM alternativas),  1));
