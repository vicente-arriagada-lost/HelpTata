package com.Tutoriales.ms.repositories;

import com.Tutoriales.ms.models.entities.Configuracion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//* Repositorio JPA para la tabla "configuracion".
//* Spring genera automáticamente la implementación en tiempo de ejecución.
public interface ConfiguracionRepository extends JpaRepository<Configuracion, Integer> {

    //* Busca una configuración por su clave única
    Optional<Configuracion> findByClaveConfig(String claveConfig);
}
