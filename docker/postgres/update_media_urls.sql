-- Ejecutar este script después de levantar los contenedores
-- Reemplaza TU_IP_AQUI con la IP real del servidor
-- Uso: psql -h localhost -U postgres -d helptata_tutoriales -f update_media_urls.sql

\set server_ip 'TU_IP_AQUI'

-- Actualizar URLs de videos en configuracion
UPDATE configuracion
SET valor = REPLACE(valor, '192.168.243.23', :'server_ip')
WHERE clave IN ('url_video_tutorial', 'url_video_cuestionario');

-- Actualizar URLs de imágenes en fotos
UPDATE fotos
SET foto = REPLACE(foto, '192.168.243.23', :'server_ip');

-- Actualizar URLs de videos en tutoriales
UPDATE tutoriales
SET tutorial = REPLACE(tutorial, '192.168.243.23', :'server_ip')
WHERE tutorial LIKE '%192.168.243.23%';

SELECT 'URLs actualizadas correctamente.' AS resultado;
