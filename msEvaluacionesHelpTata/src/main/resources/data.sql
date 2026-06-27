-- ============================================================
-- ms-Evaluaciones: datos de prueba
-- ============================================================

INSERT INTO evaluaciones (id_eva, nombre_eva, tipo_eva, nivel_eva, banco_preg, id_tutor)
VALUES
  (1, 'Evaluación Estafas Digitales',  'QUIZ', 'BASICO', 5, 1),
  (2, 'Evaluación Uso de Smartphone',  'QUIZ', 'BASICO', 4, 2),
  (3, 'Evaluación Internet Seguro',    'QUIZ', 'BASICO', 4, 3)
ON CONFLICT (id_eva) DO NOTHING;

SELECT setval(pg_get_serial_sequence('evaluaciones', 'id_eva'), COALESCE((SELECT MAX(id_eva) FROM evaluaciones), 1));
