package com.PreguntasRespuestas.ms.repositories;

import com.PreguntasRespuestas.ms.models.entities.RespuestaUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RespuestaUsuarioRepository extends JpaRepository<RespuestaUsuario, Integer> {

    @Query("SELECT r FROM RespuestaUsuario r WHERE r.id_resultado = :idResultado")
    List<RespuestaUsuario> findByIdResultado(int idResultado);

    @Query("SELECT r FROM RespuestaUsuario r WHERE r.id_usuario = :idUsuario")
    List<RespuestaUsuario> findByIdUsuario(int idUsuario);
}
