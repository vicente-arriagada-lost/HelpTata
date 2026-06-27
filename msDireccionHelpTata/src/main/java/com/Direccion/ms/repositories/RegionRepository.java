package com.Direccion.ms.repositories;

import com.Direccion.ms.models.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Integer> {

    @Query("SELECT r FROM Region r WHERE r.pais.id_pais = :idPais")
    List<Region> findByPais_IdPais(@Param("idPais") int idPais);

}
