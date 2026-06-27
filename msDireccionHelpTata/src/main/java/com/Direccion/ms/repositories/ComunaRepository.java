package com.Direccion.ms.repositories;

import com.Direccion.ms.models.entities.Comuna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ComunaRepository extends JpaRepository<Comuna, Integer> {

    @Query("SELECT c FROM Comuna c WHERE c.ciudad.id_ciudad = :idCiudad")
    List<Comuna> findByCiudad_IdCiudad(@Param("idCiudad") int idCiudad);

}
