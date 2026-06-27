-- ============================================================
-- ms-Usuario: datos de prueba
-- ============================================================

INSERT INTO rol (id_rol, tipo_rol)
VALUES (1, 'ADMIN'),
       (2, 'USER'),
       (3, 'MODERATOR')
ON CONFLICT (id_rol) DO NOTHING;

INSERT INTO email (id_email, email)
VALUES (1, 'juan.perez@helptata.cl'),
       (2, 'maria.gonzalez@helptata.cl'),
       (3, 'pedro.silva@helptata.cl')
ON CONFLICT (id_email) DO NOTHING;

INSERT INTO usuarios (id_usuario, run_usuario, dvrun_usuario, pnombre_usuario, snombre_usuario, papellido_usuario, sapellido_usuario, fecha_nac_usuario, telefono_usuario, password_usuario, fecha_reg_usuario, id_direccion)
VALUES
  (1, '12345678', '9', 'Juan',   'Andrés', 'Pérez',    'López',   '1955-03-15', 912345678, 'test123', '2024-01-10', 1),
  (2, '87654321', 'K', 'María',  NULL,     'González', 'Rojas',   '1960-07-22', 987654321, 'test123', '2024-01-11', 2),
  (3, '11223344', '5', 'Pedro',  'José',   'Silva',    NULL,      '1948-11-30', 911223344, 'test123', '2024-01-12', 3)
ON CONFLICT (id_usuario) DO NOTHING;

SELECT setval(pg_get_serial_sequence('rol',       'id_rol'),      COALESCE((SELECT MAX(id_rol)      FROM rol),      1));
SELECT setval(pg_get_serial_sequence('email',     'id_email'),    COALESCE((SELECT MAX(id_email)    FROM email),    1));
SELECT setval(pg_get_serial_sequence('usuarios',  'id_usuario'),  COALESCE((SELECT MAX(id_usuario)  FROM usuarios), 1));
