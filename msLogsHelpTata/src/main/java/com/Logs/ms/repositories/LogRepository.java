package com.Logs.ms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Logs.ms.models.entities.Log;

import java.util.List;

//* Repositorio JPA para la entidad Log
//? JpaRepository<Log, Integer> provee automáticamente:
//? findAll(), findById(), save(), deleteById(), existsById(), count(), etc.
//? Se usan @Query explícitas porque los campos con guión bajo generan ambigüedad
//? en los métodos derivados (Spring Data interpreta '_' como separador de relaciones)
@Repository
public interface LogRepository extends JpaRepository<Log, Integer> {

    //* Retorna todos los logs de un tipo específico (ej: "ERROR", "INFO")
    @Query("SELECT l FROM Log l WHERE l.tipo_log = :tipo")
    List<Log> findByTipoLog(@Param("tipo") String tipo);

    //* Retorna todos los logs asociados a un usuario específico
    @Query("SELECT l FROM Log l WHERE l.id_usuario = :idUsuario")
    List<Log> findByIdUsuario(@Param("idUsuario") Integer idUsuario);

    //* Retorna todos los logs de un microservicio específico (ej: "ms-usuario")
    @Query("SELECT l FROM Log l WHERE l.servicio_origen = :servicio")
    List<Log> findByServicioOrigen(@Param("servicio") String servicio);

}
