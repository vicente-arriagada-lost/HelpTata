package com.Evaluaciones.ms.repositories;

import com.Evaluaciones.ms.models.entities.Evaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface EvaluacionRepository extends JpaRepository<Evaluacion, Integer> {

    @Query("SELECT e FROM Evaluacion e WHERE e.id_tutor = :idTutor")
    List<Evaluacion> findByIdTutor(@Param("idTutor") int idTutor);

    @Query("SELECT e FROM Evaluacion e WHERE e.nivel_eva = :nivelEva")
    List<Evaluacion> findByNivel_eva(@Param("nivelEva") String nivelEva);

    @Query("SELECT e FROM Evaluacion e WHERE e.tipo_eva = :tipoEva")
    List<Evaluacion> findByTipo_eva(@Param("tipoEva") String tipoEva);

}
