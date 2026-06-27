package com.Tutoriales.ms.repositories;

import com.Tutoriales.ms.models.entities.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface FotoRepository extends JpaRepository<Foto, Integer> {

    @Query("SELECT f FROM Foto f WHERE f.tutorial.id_tutor = :idTutor")
    List<Foto> findByTutorial_IdTutor(@Param("idTutor") int idTutor);

}
