package com.Direccion.ms.repositories;

import com.Direccion.ms.models.entities.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DireccionRepository extends JpaRepository<Direccion, Integer> {

    @Query("SELECT d FROM Direccion d WHERE d.comuna.id_comuna = :idComuna")
    List<Direccion> findByComuna_IdComuna(@Param("idComuna") int idComuna);

}
