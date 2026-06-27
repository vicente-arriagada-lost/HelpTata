-- ============================================================
-- ms-Logs: datos de prueba
-- ============================================================

INSERT INTO logs (id_log, tipo_log, servicio_origen, mensaje_log, fecha_hora_log, id_usuario, ip_log, detalle_log)
VALUES
  (1, 'INFO',    'ms-usuario',  'Usuario creado exitosamente',        '2024-01-10 10:00:00', 1, '127.0.0.1', NULL),
  (2, 'INFO',    'ms-usuario',  'Usuario creado exitosamente',        '2024-01-11 10:05:00', 2, '127.0.0.1', NULL),
  (3, 'WARNING', 'ms-usuario',  'Intento de login con clave errónea', '2024-01-12 14:30:00', 3, '192.168.1.5', 'Tercer intento fallido'),
  (4, 'INFO',    'ms-progreso', 'Progreso registrado',                '2024-01-13 09:00:00', 1, '127.0.0.1', NULL),
  (5, 'ERROR',   'ms-tutoriales','Error al cargar recurso multimedia', '2024-01-14 16:45:00', NULL, '10.0.0.1', 'java.io.IOException: Connection timeout')
ON CONFLICT (id_log) DO NOTHING;

SELECT setval(pg_get_serial_sequence('logs', 'id_log'), COALESCE((SELECT MAX(id_log) FROM logs), 1));
