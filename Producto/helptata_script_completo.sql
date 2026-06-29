-- =============================================================================
-- HELPTATA - SCRIPT COMPLETO DE BASE DE DATOS
-- PostgreSQL 14+
-- Incluye: creación de bases de datos, tablas, procedimientos almacenados
--          y datos de prueba para todos los microservicios.
--
-- MICROSERVICIOS:
--   ms-usuario      → helptata_usuario
--   ms-direccion    → helptata_direccion
--   ms-tutoriales   → helptata_tutoriales
--   ms-progreso     → helptata_progreso
--   ms-evaluaciones → helptata_evaluaciones
--   ms-logs         → helptata_logs
--   ms-preguntas    → helptata_preguntas
--
-- NOTA: Las contraseñas están cifradas con BCrypt (Spring Security).
--       El texto plano de los usuarios de prueba es: Helptata123!
-- =============================================================================


-- =============================================================================
-- 1. CREAR BASES DE DATOS
-- =============================================================================

CREATE DATABASE helptata_usuario;
CREATE DATABASE helptata_direccion;
CREATE DATABASE helptata_tutoriales;
CREATE DATABASE helptata_progreso;
CREATE DATABASE helptata_evaluaciones;
CREATE DATABASE helptata_logs;
CREATE DATABASE helptata_preguntas;


-- =============================================================================
-- 2. BASE DE DATOS: helptata_usuario
-- =============================================================================
\connect helptata_usuario;

-- ─── TABLAS ──────────────────────────────────────────────────────────────────

CREATE TABLE rol (
    id_rol   SERIAL       PRIMARY KEY,
    tipo_rol VARCHAR(255) NOT NULL
);

CREATE TABLE email (
    id_email SERIAL       PRIMARY KEY,
    email    VARCHAR(255) NOT NULL
);

CREATE TABLE usuarios (
    id_usuario        SERIAL       PRIMARY KEY,
    run_usuario       VARCHAR(20)  NOT NULL,
    dvrun_usuario     VARCHAR(1)   NOT NULL,
    pnombre_usuario   VARCHAR(100) NOT NULL,
    snombre_usuario   VARCHAR(100),
    papellido_usuario VARCHAR(100) NOT NULL,
    sapellido_usuario VARCHAR(100),
    fecha_nac_usuario DATE         NOT NULL,
    telefono_usuario  VARCHAR(20)  NOT NULL,
    password_usuario  VARCHAR(255) NOT NULL,
    fecha_reg_usuario DATE         NOT NULL,
    id_direccion      INTEGER,
    id_rol            INTEGER REFERENCES rol(id_rol)
);

-- ─── PROCEDIMIENTOS ALMACENADOS ───────────────────────────────────────────────

-- PA: obtener usuario por ID
CREATE OR REPLACE FUNCTION pa_obtener_usuario(p_id INTEGER)
RETURNS TABLE (
    id_usuario        INTEGER,
    run_usuario       VARCHAR,
    dvrun_usuario     VARCHAR,
    pnombre_usuario   VARCHAR,
    snombre_usuario   VARCHAR,
    papellido_usuario VARCHAR,
    sapellido_usuario VARCHAR,
    fecha_nac_usuario DATE,
    telefono_usuario  VARCHAR,
    fecha_reg_usuario DATE,
    id_direccion      INTEGER,
    id_rol            INTEGER
) AS $$
BEGIN
    RETURN QUERY
    SELECT u.id_usuario, u.run_usuario, u.dvrun_usuario,
           u.pnombre_usuario, u.snombre_usuario,
           u.papellido_usuario, u.sapellido_usuario,
           u.fecha_nac_usuario, u.telefono_usuario,
           u.fecha_reg_usuario, u.id_direccion, u.id_rol
    FROM usuarios u
    WHERE u.id_usuario = p_id;
END;
$$ LANGUAGE plpgsql;

-- PA: obtener todos los usuarios (sin contraseña)
CREATE OR REPLACE FUNCTION pa_listar_usuarios()
RETURNS TABLE (
    id_usuario        INTEGER,
    run_usuario       VARCHAR,
    pnombre_usuario   VARCHAR,
    papellido_usuario VARCHAR,
    telefono_usuario  VARCHAR,
    fecha_reg_usuario DATE
) AS $$
BEGIN
    RETURN QUERY
    SELECT u.id_usuario, u.run_usuario,
           u.pnombre_usuario, u.papellido_usuario,
           u.telefono_usuario, u.fecha_reg_usuario
    FROM usuarios u
    ORDER BY u.id_usuario;
END;
$$ LANGUAGE plpgsql;

-- PA: eliminar usuario por ID
CREATE OR REPLACE PROCEDURE pa_eliminar_usuario(p_id INTEGER)
LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM usuarios WHERE id_usuario = p_id;
END;
$$;

-- ─── DATOS DE PRUEBA ─────────────────────────────────────────────────────────

INSERT INTO rol (tipo_rol) VALUES
    ('ADMIN'),
    ('USER');

INSERT INTO email (email) VALUES
    ('admin@helptata.cl'),
    ('juan.perez@gmail.com'),
    ('maria.gonzalez@gmail.com'),
    ('pedro.ramirez@outlook.com'),
    ('ana.torres@yahoo.com');

-- Contraseña en texto plano: Helptata123!
-- Hash BCrypt generado con strength 10
INSERT INTO usuarios (run_usuario, dvrun_usuario, pnombre_usuario, snombre_usuario,
                      papellido_usuario, sapellido_usuario, fecha_nac_usuario,
                      telefono_usuario, password_usuario, fecha_reg_usuario,
                      id_direccion, id_rol)
VALUES
    ('11111111', '1', 'Admin',  NULL,      'HelpTata',  NULL,        '1990-01-01',
     '+56911111111', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LjZAwiomDXa', '2025-01-01', NULL, 1),
    ('12345678', '9', 'Juan',   'Carlos',  'Pérez',     'González',  '1985-06-15',
     '+56922222222', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LjZAwiomDXa', '2025-03-10', 1,    2),
    ('98765432', 'K', 'María',  'Isabel',  'González',  'Ramírez',   '1992-11-23',
     '+56933333333', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LjZAwiomDXa', '2025-04-05', 2,    2),
    ('55544433', '2', 'Pedro',  NULL,      'Ramírez',   'Torres',    '1978-03-30',
     '+56944444444', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LjZAwiomDXa', '2025-05-20', 3,    2),
    ('76543210', '5', 'Ana',    'Lucía',   'Torres',    NULL,        '2000-09-12',
     '+56955555555', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LjZAwiomDXa', '2025-06-01', NULL, 2);


-- =============================================================================
-- 3. BASE DE DATOS: helptata_direccion
-- =============================================================================
\connect helptata_direccion;

-- ─── TABLAS ──────────────────────────────────────────────────────────────────

CREATE TABLE paises (
    id_pais     SERIAL       PRIMARY KEY,
    nombre_pais VARCHAR(100) NOT NULL
);

CREATE TABLE regiones (
    id_region     SERIAL       PRIMARY KEY,
    nombre_region VARCHAR(150) NOT NULL,
    id_pais       INTEGER      NOT NULL REFERENCES paises(id_pais)
);

CREATE TABLE ciudades (
    id_ciudad     SERIAL       PRIMARY KEY,
    nombre_ciudad VARCHAR(150) NOT NULL,
    id_region     INTEGER      NOT NULL REFERENCES regiones(id_region)
);

CREATE TABLE comunas (
    id_comuna     SERIAL       PRIMARY KEY,
    nombre_comuna VARCHAR(150) NOT NULL,
    id_ciudad     INTEGER      NOT NULL REFERENCES ciudades(id_ciudad)
);

CREATE TABLE direcciones (
    id_direccion SERIAL       PRIMARY KEY,
    calle        VARCHAR(255) NOT NULL,
    numero       VARCHAR(20)  NOT NULL,
    id_comuna    INTEGER      NOT NULL REFERENCES comunas(id_comuna)
);

-- ─── PROCEDIMIENTOS ALMACENADOS ───────────────────────────────────────────────

-- PA: obtener dirección completa por ID
CREATE OR REPLACE FUNCTION pa_obtener_direccion_completa(p_id INTEGER)
RETURNS TABLE (
    id_direccion  INTEGER,
    calle         VARCHAR,
    numero        VARCHAR,
    nombre_comuna VARCHAR,
    nombre_ciudad VARCHAR,
    nombre_region VARCHAR,
    nombre_pais   VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT d.id_direccion, d.calle, d.numero,
           co.nombre_comuna, ci.nombre_ciudad,
           r.nombre_region, p.nombre_pais
    FROM direcciones d
    JOIN comunas  co ON co.id_comuna  = d.id_comuna
    JOIN ciudades ci ON ci.id_ciudad  = co.id_ciudad
    JOIN regiones r  ON r.id_region   = ci.id_region
    JOIN paises   p  ON p.id_pais     = r.id_pais
    WHERE d.id_direccion = p_id;
END;
$$ LANGUAGE plpgsql;

-- PA: listar comunas de una ciudad
CREATE OR REPLACE FUNCTION pa_listar_comunas_por_ciudad(p_id_ciudad INTEGER)
RETURNS TABLE (id_comuna INTEGER, nombre_comuna VARCHAR) AS $$
BEGIN
    RETURN QUERY
    SELECT c.id_comuna, c.nombre_comuna
    FROM comunas c
    WHERE c.id_ciudad = p_id_ciudad
    ORDER BY c.nombre_comuna;
END;
$$ LANGUAGE plpgsql;

-- ─── DATOS DE PRUEBA ─────────────────────────────────────────────────────────

INSERT INTO paises (nombre_pais) VALUES ('Chile');

INSERT INTO regiones (nombre_region, id_pais) VALUES
    ('Región Metropolitana de Santiago', 1),
    ('Región de Valparaíso',             1),
    ('Región del Biobío',                1);

INSERT INTO ciudades (nombre_ciudad, id_region) VALUES
    ('Santiago',     1),
    ('Valparaíso',   2),
    ('Concepción',   3);

INSERT INTO comunas (nombre_comuna, id_ciudad) VALUES
    ('Providencia',  1),
    ('Las Condes',   1),
    ('Maipú',        1),
    ('Valparaíso',   2),
    ('Viña del Mar', 2),
    ('Concepción',   3);

INSERT INTO direcciones (calle, numero, id_comuna) VALUES
    ('Av. Providencia',      '1234',  1),
    ('Los Leones',           '456',   2),
    ('Av. Pajaritos',        '789',   3);


-- =============================================================================
-- 4. BASE DE DATOS: helptata_tutoriales
-- =============================================================================
\connect helptata_tutoriales;

-- ─── TABLAS ──────────────────────────────────────────────────────────────────

CREATE TABLE tutoriales (
    id_tutor        SERIAL        PRIMARY KEY,
    nombre_tuto     VARCHAR(255)  NOT NULL,
    cat_tuto        VARCHAR(100)  NOT NULL,
    nivel_tuto      VARCHAR(50),
    tutorial        VARCHAR(255),
    tiempo_tutorial INTEGER,
    descripcion_tuto VARCHAR(1000)
);

CREATE TABLE fotos (
    id_foto  SERIAL  PRIMARY KEY,
    foto     TEXT    NOT NULL,
    id_tutor INTEGER NOT NULL REFERENCES tutoriales(id_tutor)
);

CREATE TABLE configuracion (
    id_config    SERIAL       PRIMARY KEY,
    clave_config VARCHAR(255) NOT NULL UNIQUE,
    valor_config VARCHAR(500) NOT NULL
);

-- ─── PROCEDIMIENTOS ALMACENADOS ───────────────────────────────────────────────

-- PA: buscar tutoriales por categoría
CREATE OR REPLACE FUNCTION pa_tutoriales_por_categoria(p_categoria VARCHAR)
RETURNS TABLE (
    id_tutor         INTEGER,
    nombre_tuto      VARCHAR,
    nivel_tuto       VARCHAR,
    tiempo_tutorial  INTEGER,
    descripcion_tuto VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT t.id_tutor, t.nombre_tuto, t.nivel_tuto,
           t.tiempo_tutorial, t.descripcion_tuto
    FROM tutoriales t
    WHERE LOWER(t.cat_tuto) = LOWER(p_categoria)
    ORDER BY t.id_tutor;
END;
$$ LANGUAGE plpgsql;

-- PA: obtener fotos de un tutorial
CREATE OR REPLACE FUNCTION pa_fotos_por_tutorial(p_id_tutor INTEGER)
RETURNS TABLE (id_foto INTEGER, foto TEXT) AS $$
BEGIN
    RETURN QUERY
    SELECT f.id_foto, f.foto
    FROM fotos f
    WHERE f.id_tutor = p_id_tutor;
END;
$$ LANGUAGE plpgsql;

-- ─── DATOS DE PRUEBA ─────────────────────────────────────────────────────────

INSERT INTO tutoriales (nombre_tuto, cat_tuto, nivel_tuto, tutorial, tiempo_tutorial, descripcion_tuto) VALUES
    ('Introducción a Python',          'Programación', 'BASICO',       'https://helptata.cl/tutoriales/python-intro',     60,  'Tutorial introductorio al lenguaje de programación Python.'),
    ('Programación Orientada a Objetos','Programación', 'INTERMEDIO',  'https://helptata.cl/tutoriales/poo',              90,  'Conceptos fundamentales de POO: clases, herencia y polimorfismo.'),
    ('Diseño UI con Figma',            'Diseño',        'BASICO',       'https://helptata.cl/tutoriales/figma',            45,  'Aprende a diseñar interfaces de usuario con Figma.'),
    ('Bases de Datos Relacionales',    'Bases de Datos','INTERMEDIO',   'https://helptata.cl/tutoriales/bd-relacionales',  75,  'Diseño y consulta de bases de datos con SQL.'),
    ('Microservicios con Spring Boot', 'Backend',       'AVANZADO',     'https://helptata.cl/tutoriales/spring-ms',       120,  'Arquitectura de microservicios usando Spring Boot y Docker.');

INSERT INTO configuracion (clave_config, valor_config) VALUES
    ('video_cuestionario_url', 'https://helptata.cl/videos/instrucciones-cuestionario.mp4'),
    ('max_intentos_evaluacion', '3'),
    ('tiempo_expiracion_token_min', '60');


-- =============================================================================
-- 5. BASE DE DATOS: helptata_progreso
-- =============================================================================
\connect helptata_progreso;

-- ─── TABLAS ──────────────────────────────────────────────────────────────────

CREATE TABLE progreso (
    id_progreso                SERIAL           PRIMARY KEY,
    id_usuario                 INTEGER          NOT NULL,
    id_tutorial                INTEGER          NOT NULL,
    recursos_completados       INTEGER          NOT NULL DEFAULT 0,
    cantidad_recursos_totales  INTEGER          NOT NULL DEFAULT 0,
    preguntas_acertadas        INTEGER          NOT NULL DEFAULT 0,
    preguntas_falladas         INTEGER          NOT NULL DEFAULT 0,
    porcentaje_progreso        DOUBLE PRECISION NOT NULL DEFAULT 0,
    fecha_ultima_actividad     TIMESTAMP        NOT NULL
);

-- ─── PROCEDIMIENTOS ALMACENADOS ───────────────────────────────────────────────

-- PA: progreso de un usuario en todos sus tutoriales
CREATE OR REPLACE FUNCTION pa_progreso_por_usuario(p_id_usuario INTEGER)
RETURNS TABLE (
    id_progreso           INTEGER,
    id_tutorial           INTEGER,
    recursos_completados  INTEGER,
    cantidad_recursos_totales INTEGER,
    porcentaje_progreso   DOUBLE PRECISION,
    fecha_ultima_actividad TIMESTAMP
) AS $$
BEGIN
    RETURN QUERY
    SELECT p.id_progreso, p.id_tutorial,
           p.recursos_completados, p.cantidad_recursos_totales,
           p.porcentaje_progreso, p.fecha_ultima_actividad
    FROM progreso p
    WHERE p.id_usuario = p_id_usuario
    ORDER BY p.fecha_ultima_actividad DESC;
END;
$$ LANGUAGE plpgsql;

-- PA: recalcular porcentaje de progreso
CREATE OR REPLACE PROCEDURE pa_recalcular_porcentaje(p_id_progreso INTEGER)
LANGUAGE plpgsql AS $$
DECLARE
    v_completados INTEGER;
    v_totales     INTEGER;
BEGIN
    SELECT recursos_completados, cantidad_recursos_totales
    INTO v_completados, v_totales
    FROM progreso WHERE id_progreso = p_id_progreso;

    IF v_totales > 0 THEN
        UPDATE progreso
        SET porcentaje_progreso = ROUND((v_completados::NUMERIC / v_totales) * 100, 2)
        WHERE id_progreso = p_id_progreso;
    END IF;
END;
$$;

-- ─── DATOS DE PRUEBA ─────────────────────────────────────────────────────────

INSERT INTO progreso (id_usuario, id_tutorial, recursos_completados, cantidad_recursos_totales,
                      preguntas_acertadas, preguntas_falladas, porcentaje_progreso, fecha_ultima_actividad)
VALUES
    (2, 1, 4,  5,  8,  2,  80.0,  '2025-06-10 14:30:00'),
    (2, 2, 2,  5,  3,  5,  40.0,  '2025-06-15 09:00:00'),
    (3, 1, 5,  5,  10, 0,  100.0, '2025-06-12 11:45:00'),
    (3, 3, 1,  3,  2,  1,  33.3,  '2025-06-20 16:00:00'),
    (4, 4, 3,  4,  6,  2,  75.0,  '2025-06-22 10:30:00');


-- =============================================================================
-- 6. BASE DE DATOS: helptata_evaluaciones
-- =============================================================================
\connect helptata_evaluaciones;

-- ─── TABLAS ──────────────────────────────────────────────────────────────────

CREATE TABLE evaluaciones (
    id_eva     SERIAL       PRIMARY KEY,
    nombre_eva VARCHAR(255) NOT NULL,
    tipo_eva   VARCHAR(50)  NOT NULL,
    nivel_eva  VARCHAR(50)  NOT NULL,
    banco_preg INTEGER      NOT NULL,
    id_tutor   INTEGER      NOT NULL
);

-- ─── PROCEDIMIENTOS ALMACENADOS ───────────────────────────────────────────────

-- PA: evaluaciones de un tutorial
CREATE OR REPLACE FUNCTION pa_evaluaciones_por_tutorial(p_id_tutor INTEGER)
RETURNS TABLE (
    id_eva     INTEGER,
    nombre_eva VARCHAR,
    tipo_eva   VARCHAR,
    nivel_eva  VARCHAR,
    banco_preg INTEGER
) AS $$
BEGIN
    RETURN QUERY
    SELECT e.id_eva, e.nombre_eva, e.tipo_eva, e.nivel_eva, e.banco_preg
    FROM evaluaciones e
    WHERE e.id_tutor = p_id_tutor
    ORDER BY e.id_eva;
END;
$$ LANGUAGE plpgsql;

-- ─── DATOS DE PRUEBA ─────────────────────────────────────────────────────────

INSERT INTO evaluaciones (nombre_eva, tipo_eva, nivel_eva, banco_preg, id_tutor) VALUES
    ('Quiz Inicial Python',                'QUIZ',           'BASICO',      10, 1),
    ('Evaluación Final Python',            'EXAMEN',         'BASICO',      20, 1),
    ('Quiz POO - Clases y Objetos',        'QUIZ',           'INTERMEDIO',  10, 2),
    ('Práctica POO - Herencia',            'PRACTICA',       'INTERMEDIO',  15, 2),
    ('Autoevaluación Diseño UI',           'AUTOEVALUACION', 'BASICO',       8, 3),
    ('Examen SQL Intermedio',              'EXAMEN',         'INTERMEDIO',  25, 4),
    ('Quiz Microservicios con Docker',     'QUIZ',           'AVANZADO',    12, 5);


-- =============================================================================
-- 7. BASE DE DATOS: helptata_logs
-- =============================================================================
\connect helptata_logs;

-- ─── TABLAS ──────────────────────────────────────────────────────────────────

CREATE TABLE logs (
    id_log          SERIAL        PRIMARY KEY,
    tipo_log        VARCHAR(50)   NOT NULL,
    servicio_origen VARCHAR(100)  NOT NULL,
    mensaje_log     VARCHAR(500)  NOT NULL,
    fecha_hora_log  TIMESTAMP     NOT NULL,
    id_usuario      INTEGER,
    ip_log          VARCHAR(50),
    detalle_log     VARCHAR(2000)
);

-- ─── PROCEDIMIENTOS ALMACENADOS ───────────────────────────────────────────────

-- PA: logs por tipo y rango de fechas
CREATE OR REPLACE FUNCTION pa_logs_por_tipo(p_tipo VARCHAR, p_desde TIMESTAMP, p_hasta TIMESTAMP)
RETURNS TABLE (
    id_log          INTEGER,
    servicio_origen VARCHAR,
    mensaje_log     VARCHAR,
    fecha_hora_log  TIMESTAMP,
    id_usuario      INTEGER,
    ip_log          VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT l.id_log, l.servicio_origen, l.mensaje_log,
           l.fecha_hora_log, l.id_usuario, l.ip_log
    FROM logs l
    WHERE l.tipo_log = p_tipo
      AND l.fecha_hora_log BETWEEN p_desde AND p_hasta
    ORDER BY l.fecha_hora_log DESC;
END;
$$ LANGUAGE plpgsql;

-- PA: logs de un usuario específico
CREATE OR REPLACE FUNCTION pa_logs_por_usuario(p_id_usuario INTEGER)
RETURNS TABLE (
    id_log          INTEGER,
    tipo_log        VARCHAR,
    servicio_origen VARCHAR,
    mensaje_log     VARCHAR,
    fecha_hora_log  TIMESTAMP
) AS $$
BEGIN
    RETURN QUERY
    SELECT l.id_log, l.tipo_log, l.servicio_origen,
           l.mensaje_log, l.fecha_hora_log
    FROM logs l
    WHERE l.id_usuario = p_id_usuario
    ORDER BY l.fecha_hora_log DESC;
END;
$$ LANGUAGE plpgsql;

-- ─── DATOS DE PRUEBA ─────────────────────────────────────────────────────────

INSERT INTO logs (tipo_log, servicio_origen, mensaje_log, fecha_hora_log, id_usuario, ip_log, detalle_log) VALUES
    ('AUTENTICACION', 'ms-usuario',     'Login exitoso',                        '2025-06-20 08:00:00', 2,    '200.1.1.10',  NULL),
    ('AUTENTICACION', 'ms-usuario',     'Login exitoso',                        '2025-06-20 08:05:00', 3,    '200.1.1.11',  NULL),
    ('INFO',          'ms-tutoriales',  'Tutorial consultado: id=1',            '2025-06-20 08:10:00', 2,    '200.1.1.10',  NULL),
    ('INFO',          'ms-progreso',    'Progreso actualizado: usuario=2',      '2025-06-20 08:15:00', 2,    '200.1.1.10',  NULL),
    ('ERROR',         'ms-evaluaciones','Error al obtener evaluacion: id=99',   '2025-06-20 09:00:00', 4,    '200.1.1.12',  'NotFoundException: Evaluacion 99 no existe'),
    ('WARNING',       'ms-usuario',     'Intento de login fallido',             '2025-06-20 09:30:00', NULL, '200.1.1.99',  'BadCredentialsException para email: hacker@test.com'),
    ('INFO',          'ms-preguntas',   'Cuestionario enviado: usuario=3',      '2025-06-21 10:00:00', 3,    '200.1.1.11',  NULL),
    ('AUTENTICACION', 'ms-usuario',     'Logout: sesion cerrada',               '2025-06-21 17:00:00', 2,    '200.1.1.10',  NULL);


-- =============================================================================
-- 8. BASE DE DATOS: helptata_preguntas
-- =============================================================================
\connect helptata_preguntas;

-- ─── TABLAS ──────────────────────────────────────────────────────────────────

CREATE TABLE cuestionarios (
    id_cuestionario        SERIAL        PRIMARY KEY,
    titulo_cuestionario    VARCHAR(255)  NOT NULL,
    descripcion_cuestionario VARCHAR(1000),
    id_tutor               INTEGER       NOT NULL
);

CREATE TABLE preguntas (
    id_pregunta        SERIAL       PRIMARY KEY,
    enunciado_pregunta VARCHAR(255) NOT NULL,
    id_cuestionario    INTEGER      NOT NULL REFERENCES cuestionarios(id_cuestionario)
);

CREATE TABLE alternativas (
    id_alternativa    SERIAL       PRIMARY KEY,
    texto_alternativa VARCHAR(255) NOT NULL,
    es_correcta       BOOLEAN      NOT NULL DEFAULT FALSE,
    id_pregunta       INTEGER      NOT NULL REFERENCES preguntas(id_pregunta)
);

CREATE TABLE resultados_cuestionario (
    id_resultado    SERIAL           PRIMARY KEY,
    id_usuario      INTEGER          NOT NULL,
    id_cuestionario INTEGER          NOT NULL REFERENCES cuestionarios(id_cuestionario),
    correctas       INTEGER          NOT NULL DEFAULT 0,
    incorrectas     INTEGER          NOT NULL DEFAULT 0,
    porcentaje      DOUBLE PRECISION NOT NULL DEFAULT 0,
    fecha_resultado TIMESTAMP        NOT NULL
);

CREATE TABLE respuestas_usuario (
    id_respuesta                INTEGER NOT NULL,
    id_usuario                  INTEGER NOT NULL,
    id_pregunta                 INTEGER NOT NULL REFERENCES preguntas(id_pregunta),
    id_alternativa_seleccionada INTEGER NOT NULL REFERENCES alternativas(id_alternativa),
    id_resultado                INTEGER NOT NULL REFERENCES resultados_cuestionario(id_resultado),
    PRIMARY KEY (id_respuesta)
);

-- ─── PROCEDIMIENTOS ALMACENADOS ───────────────────────────────────────────────

-- PA: obtener preguntas con alternativas de un cuestionario
CREATE OR REPLACE FUNCTION pa_cuestionario_completo(p_id_cuestionario INTEGER)
RETURNS TABLE (
    id_pregunta        INTEGER,
    enunciado_pregunta VARCHAR,
    id_alternativa     INTEGER,
    texto_alternativa  VARCHAR,
    es_correcta        BOOLEAN
) AS $$
BEGIN
    RETURN QUERY
    SELECT p.id_pregunta, p.enunciado_pregunta,
           a.id_alternativa, a.texto_alternativa, a.es_correcta
    FROM preguntas p
    JOIN alternativas a ON a.id_pregunta = p.id_pregunta
    WHERE p.id_cuestionario = p_id_cuestionario
    ORDER BY p.id_pregunta, a.id_alternativa;
END;
$$ LANGUAGE plpgsql;

-- PA: resultado de un usuario en un cuestionario
CREATE OR REPLACE FUNCTION pa_resultado_usuario(p_id_usuario INTEGER, p_id_cuestionario INTEGER)
RETURNS TABLE (
    id_resultado    INTEGER,
    correctas       INTEGER,
    incorrectas     INTEGER,
    porcentaje      DOUBLE PRECISION,
    fecha_resultado TIMESTAMP
) AS $$
BEGIN
    RETURN QUERY
    SELECT r.id_resultado, r.correctas, r.incorrectas,
           r.porcentaje, r.fecha_resultado
    FROM resultados_cuestionario r
    WHERE r.id_usuario = p_id_usuario
      AND r.id_cuestionario = p_id_cuestionario
    ORDER BY r.fecha_resultado DESC;
END;
$$ LANGUAGE plpgsql;

-- PA: ranking de resultados de un cuestionario
CREATE OR REPLACE FUNCTION pa_ranking_cuestionario(p_id_cuestionario INTEGER)
RETURNS TABLE (
    id_usuario  INTEGER,
    porcentaje  DOUBLE PRECISION,
    correctas   INTEGER,
    fecha_resultado TIMESTAMP
) AS $$
BEGIN
    RETURN QUERY
    SELECT r.id_usuario, r.porcentaje, r.correctas, r.fecha_resultado
    FROM resultados_cuestionario r
    WHERE r.id_cuestionario = p_id_cuestionario
    ORDER BY r.porcentaje DESC, r.fecha_resultado ASC;
END;
$$ LANGUAGE plpgsql;

-- ─── DATOS DE PRUEBA ─────────────────────────────────────────────────────────

INSERT INTO cuestionarios (titulo_cuestionario, descripcion_cuestionario, id_tutor) VALUES
    ('Cuestionario Python Básico',     'Preguntas sobre sintaxis y fundamentos de Python.',       1),
    ('Cuestionario POO Intermedio',    'Evaluación sobre clases, herencia y polimorfismo.',       2),
    ('Cuestionario Bases de Datos',    'Consultas SQL y diseño de esquemas relacionales.',        4);

INSERT INTO preguntas (enunciado_pregunta, id_cuestionario) VALUES
    -- Cuestionario 1: Python
    ('¿Cuál es el tipo de dato correcto para almacenar texto en Python?',                           1),
    ('¿Qué operador se usa para potencia en Python?',                                               1),
    ('¿Cuál es la función para mostrar texto en consola?',                                          1),
    -- Cuestionario 2: POO
    ('¿Qué es la herencia en POO?',                                                                 2),
    ('¿Cuál es el método constructor en Java?',                                                     2),
    -- Cuestionario 3: BD
    ('¿Qué sentencia SQL se usa para consultar datos?',                                             3),
    ('¿Qué cláusula filtra resultados en una consulta SQL?',                                        3);

INSERT INTO alternativas (texto_alternativa, es_correcta, id_pregunta) VALUES
    -- Pregunta 1
    ('str',     TRUE,  1),
    ('int',     FALSE, 1),
    ('char',    FALSE, 1),
    ('text',    FALSE, 1),
    -- Pregunta 2
    ('**',      TRUE,  2),
    ('^',       FALSE, 2),
    ('*',       FALSE, 2),
    ('pow',     FALSE, 2),
    -- Pregunta 3
    ('print()', TRUE,  3),
    ('echo()',  FALSE, 3),
    ('log()',   FALSE, 3),
    ('show()',  FALSE, 3),
    -- Pregunta 4
    ('Mecanismo que permite a una clase adquirir propiedades de otra', TRUE,  4),
    ('Una función dentro de una clase',                                 FALSE, 4),
    ('Un tipo de variable global',                                      FALSE, 4),
    ('Un bucle especial de POO',                                        FALSE, 4),
    -- Pregunta 5
    ('__init__',       TRUE,  5),
    ('constructor()',  FALSE, 5),
    ('init()',         FALSE, 5),
    ('new()',          FALSE, 5),
    -- Pregunta 6
    ('SELECT',  TRUE,  6),
    ('GET',     FALSE, 6),
    ('FETCH',   FALSE, 6),
    ('QUERY',   FALSE, 6),
    -- Pregunta 7
    ('WHERE',   TRUE,  7),
    ('FILTER',  FALSE, 7),
    ('HAVING',  FALSE, 7),
    ('LIMIT',   FALSE, 7);

-- Resultado de prueba: usuario 2 respondió el cuestionario 1
INSERT INTO resultados_cuestionario (id_usuario, id_cuestionario, correctas, incorrectas, porcentaje, fecha_resultado) VALUES
    (2, 1, 2, 1, 66.67, '2025-06-21 10:00:00'),
    (3, 1, 3, 0, 100.0, '2025-06-21 11:00:00');

INSERT INTO respuestas_usuario (id_respuesta, id_usuario, id_pregunta, id_alternativa_seleccionada, id_resultado) VALUES
    -- Usuario 2, cuestionario 1
    (1, 2, 1, 1,  1),   -- str (correcta)
    (2, 2, 2, 7,  1),   -- * (incorrecta)
    (3, 2, 3, 9,  1),   -- print() (correcta)
    -- Usuario 3, cuestionario 1
    (4, 3, 1, 1,  2),   -- str (correcta)
    (5, 3, 2, 5,  2),   -- ** (correcta)
    (6, 3, 3, 9,  2);   -- print() (correcta)
