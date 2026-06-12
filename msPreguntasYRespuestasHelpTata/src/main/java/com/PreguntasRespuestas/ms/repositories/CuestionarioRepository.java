package com.PreguntasRespuestas.ms.repositories;

import com.PreguntasRespuestas.ms.models.entities.Cuestionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CuestionarioRepository extends JpaRepository<Cuestionario, Integer> {

    @Query("SELECT c FROM Cuestionario c WHERE c.id_tutor = :idTutor")
    List<Cuestionario> findByIdTutor(int idTutor);
}
