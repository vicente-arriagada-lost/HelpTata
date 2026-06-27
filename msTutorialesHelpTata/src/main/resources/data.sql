-- ============================================================
-- ms-Tutoriales: datos de prueba
-- IDs 1, 2, 3 deben coincidir con los cursos del frontend
-- ============================================================

INSERT INTO tutoriales (id_tutor, nombre_tuto, cat_tuto, nivel_tuto, tutorial, tiempo_tutorial, descripcion_tuto)
VALUES
  (1, 'Protección contra Estafas Digitales', 'Seguridad', 'BASICO',
   'http://localhost:9000/Protecci%C3%B3n%20contra%20Estafas%20Digitales.mp4', 20,
   'Aprende a reconocer correos falsos, llamadas sospechosas y sitios web fraudulentos.'),
  (2, 'Uso de Smartphones', 'Tecnología', 'BASICO',
   'http://localhost:9000/Uso%20de%20Smartphones.mp4', 25,
   'Domina tu teléfono inteligente paso a paso: llamadas, WhatsApp, correo e internet.'),
  (3, 'Internet Seguro', 'Seguridad', 'BASICO',
   'http://localhost:9000/Internet%20Seguro.mp4', 15,
   'Aprende a navegar de forma segura, crear contraseñas fuertes y proteger tu privacidad.')
ON CONFLICT (id_tutor) DO NOTHING;

INSERT INTO fotos (id_foto, foto, id_tutor)
VALUES
  (1, 'https://images.unsplash.com/photo-1614064641938-3bbee52942c7?w=800', 1),
  (2, 'https://images.unsplash.com/photo-1716558057044-72f92e6a5654?w=800', 2),
  (3, 'https://images.unsplash.com/photo-1614064641938-3bbee52942c7?w=800', 3)
ON CONFLICT (id_foto) DO NOTHING;

SELECT setval(pg_get_serial_sequence('tutoriales', 'id_tutor'), COALESCE((SELECT MAX(id_tutor) FROM tutoriales), 1));
SELECT setval(pg_get_serial_sequence('fotos',      'id_foto'),  COALESCE((SELECT MAX(id_foto)  FROM fotos),      1));

-- Configuración global: URL del video tutorial del cuestionario.
-- Cambia el valor por la URL real una vez que tengas el video subido al servidor.
INSERT INTO configuracion (clave_config, valor_config)
VALUES ('url_video_tutorial', 'http://localhost/media/tutorial.mp4')
ON CONFLICT (clave_config) DO NOTHING;

SELECT setval(pg_get_serial_sequence('configuracion', 'id_config'), COALESCE((SELECT MAX(id_config) FROM configuracion), 1));
