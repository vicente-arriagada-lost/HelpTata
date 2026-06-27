-- ============================================================
-- ms-Direccion: datos de prueba
-- ON CONFLICT DO NOTHING: los inserts se omiten si ya existen
-- ============================================================

INSERT INTO paises (id_pais, nombre_pais)
VALUES (1, 'Chile')
ON CONFLICT (id_pais) DO NOTHING;

INSERT INTO regiones (id_region, nombre_region, id_pais)
VALUES (1, 'Región Metropolitana', 1),
       (2, 'Región de Valparaíso', 1)
ON CONFLICT (id_region) DO NOTHING;

INSERT INTO ciudades (id_ciudad, nombre_ciudad, id_region)
VALUES (1, 'Santiago', 1),
       (2, 'Valparaíso', 2)
ON CONFLICT (id_ciudad) DO NOTHING;

INSERT INTO comunas (id_comuna, nombre_comuna, id_ciudad)
VALUES (1, 'Providencia', 1),
       (2, 'Las Condes', 1),
       (3, 'Viña del Mar', 2)
ON CONFLICT (id_comuna) DO NOTHING;

INSERT INTO direcciones (id_direccion, calle, numero, id_comuna)
VALUES (1, 'Av. Providencia', '1234', 1),
       (2, 'Av. Las Condes', '5678', 2),
       (3, 'Av. Libertad', '910', 3)
ON CONFLICT (id_direccion) DO NOTHING;

-- Resetear secuencias para que los próximos registros no colisionen
SELECT setval(pg_get_serial_sequence('paises',     'id_pais'),     COALESCE((SELECT MAX(id_pais)      FROM paises),     1));
SELECT setval(pg_get_serial_sequence('regiones',   'id_region'),   COALESCE((SELECT MAX(id_region)    FROM regiones),   1));
SELECT setval(pg_get_serial_sequence('ciudades',   'id_ciudad'),   COALESCE((SELECT MAX(id_ciudad)    FROM ciudades),   1));
SELECT setval(pg_get_serial_sequence('comunas',    'id_comuna'),   COALESCE((SELECT MAX(id_comuna)    FROM comunas),    1));
SELECT setval(pg_get_serial_sequence('direcciones','id_direccion'),COALESCE((SELECT MAX(id_direccion) FROM direcciones),1));
