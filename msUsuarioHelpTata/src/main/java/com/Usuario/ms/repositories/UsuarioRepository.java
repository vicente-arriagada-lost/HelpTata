package com.Usuario.ms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Usuario.ms.models.entities.Usuario;

//* Repositorio JPA para la entidad Usuario
//? JpaRepository<Usuario, Integer> provee automáticamente:
//? findAll(), findById(), save(), deleteById(), existsById(), count(), etc.
//? El segundo parámetro (Integer) es el tipo de la clave primaria (id_usuario)
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

}
