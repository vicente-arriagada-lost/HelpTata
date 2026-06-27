package com.Usuario.ms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Usuario.ms.models.entities.Rol;

//* Repositorio JPA para la entidad Rol
//? JpaRepository<Rol, Integer> provee los métodos CRUD básicos automáticamente
@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

}
