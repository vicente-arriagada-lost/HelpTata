package com.Direccion.ms.repositories;

import com.Direccion.ms.models.entities.Pais;
import org.springframework.data.jpa.repository.JpaRepository;

//* Repositorio JPA para la entidad Pais
//* JpaRepository provee automáticamente: findAll, findById, save, deleteById, existsById, etc.
//? El segundo parámetro (Integer) es el tipo de la clave primaria (id_pais)
public interface PaisRepository extends JpaRepository<Pais, Integer> {
    //* No se requieren queries adicionales para Pais — los métodos heredados son suficientes
}
