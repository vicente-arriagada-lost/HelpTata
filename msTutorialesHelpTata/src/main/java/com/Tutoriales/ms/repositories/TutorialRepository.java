package com.Tutoriales.ms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Tutoriales.ms.models.entities.Tutorial;

import java.util.List;

//* Repositorio JPA para la entidad Tutorial
//? JpaRepository<Tutorial, Integer> provee automáticamente:
//? findAll(), findById(), save(), deleteById(), existsById(), count(), etc.
//? Se usan @Query explícitas porque los campos con guión bajo generan ambigüedad
//? en los métodos derivados (Spring Data interpreta '_' como separador de relaciones)
@Repository
public interface TutorialRepository extends JpaRepository<Tutorial, Integer> {

    //* Retorna todos los tutoriales de una categoría específica
    @Query("SELECT t FROM Tutorial t WHERE t.cat_tuto = :categoria")
    List<Tutorial> findByCategoria(@Param("categoria") String categoria);

    //* Retorna todos los tutoriales de un nivel específico (ej: "BASICO")
    @Query("SELECT t FROM Tutorial t WHERE t.nivel_tuto = :nivel")
    List<Tutorial> findByNivel(@Param("nivel") String nivel);

}
