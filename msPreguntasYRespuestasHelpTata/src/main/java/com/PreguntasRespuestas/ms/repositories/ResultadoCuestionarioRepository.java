package com.PreguntasRespuestas.ms.repositories;

import com.PreguntasRespuestas.ms.models.entities.ResultadoCuestionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResultadoCuestionarioRepository extends JpaRepository<ResultadoCuestionario, Integer> {

    @Query("SELECT r FROM ResultadoCuestionario r WHERE r.id_usuario = :idUsuario")
    List<ResultadoCuestionario> findByIdUsuario(int idUsuario);

    @Query("SELECT r FROM ResultadoCuestionario r WHERE r.id_cuestionario = :idCuestionario")
    List<ResultadoCuestionario> findByIdCuestionario(int idCuestionario);

    @Query("SELECT r FROM ResultadoCuestionario r WHERE r.id_usuario = :idUsuario AND r.id_cuestionario = :idCuestionario")
    List<ResultadoCuestionario> findByIdUsuarioAndIdCuestionario(int idUsuario, int idCuestionario);
}
