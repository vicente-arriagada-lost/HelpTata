#!/bin/bash
# Crea las 7 bases de datos de HelpTata al iniciar el contenedor de PostgreSQL
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE helptata_usuario;
    CREATE DATABASE helptata_logs;
    CREATE DATABASE helptata_tutoriales;
    CREATE DATABASE helptata_direccion;
    CREATE DATABASE helptata_progreso;
    CREATE DATABASE helptata_evaluaciones;
    CREATE DATABASE helptata_preguntas;
EOSQL

echo "Bases de datos de HelpTata creadas correctamente."
