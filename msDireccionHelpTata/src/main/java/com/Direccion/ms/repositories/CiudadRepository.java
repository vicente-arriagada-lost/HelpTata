package com.Direccion.ms.repositories;

import com.Direccion.ms.models.entities.Ciudad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CiudadRepository extends JpaRepository<Ciudad, Integer> {

    @Query("SELECT c FROM Ciudad c WHERE c.region.id_region = :idRegion")
    List<Ciudad> findByRegion_IdRegion(@Param("idRegion") int idRegion);

}
