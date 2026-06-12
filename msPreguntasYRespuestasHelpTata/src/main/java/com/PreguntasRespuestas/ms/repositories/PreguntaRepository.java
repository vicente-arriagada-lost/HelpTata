package com.PreguntasRespuestas.ms.repositories;

import com.PreguntasRespuestas.ms.models.entities.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PreguntaRepository extends JpaRepository<Pregunta, Integer> {

    @Query("SELECT p FROM Pregunta p WHERE p.id_cuestionario = :idCuestionario")
    List<Pregunta> findByIdCuestionario(int idCuestionario);
}
