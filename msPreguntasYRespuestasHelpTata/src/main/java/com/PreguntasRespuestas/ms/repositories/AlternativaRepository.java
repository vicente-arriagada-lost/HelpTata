package com.PreguntasRespuestas.ms.repositories;

import com.PreguntasRespuestas.ms.models.entities.Alternativa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AlternativaRepository extends JpaRepository<Alternativa, Integer> {

    @Query("SELECT a FROM Alternativa a WHERE a.id_pregunta = :idPregunta")
    List<Alternativa> findByIdPregunta(int idPregunta);
}
