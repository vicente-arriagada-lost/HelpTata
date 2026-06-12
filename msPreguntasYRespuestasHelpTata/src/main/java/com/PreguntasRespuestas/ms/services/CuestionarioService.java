package com.PreguntasRespuestas.ms.services;

import com.PreguntasRespuestas.ms.models.dto.CuestionarioDTO;
import com.PreguntasRespuestas.ms.models.entities.Cuestionario;
import com.PreguntasRespuestas.ms.models.request.ActualizarCuestionario;
import com.PreguntasRespuestas.ms.models.request.AgregarCuestionario;
import com.PreguntasRespuestas.ms.repositories.CuestionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CuestionarioService {

    @Autowired
    private CuestionarioRepository cuestionarioRepository;

    private CuestionarioDTO toDTO(Cuestionario c) {
        return new CuestionarioDTO(
            c.getId_cuestionario(),
            c.getTitulo_cuestionario(),
            c.getDescripcion_cuestionario(),
            c.getId_tutor()
        );
    }

    public List<CuestionarioDTO> obtenerTodosLosCuestionarios() {
        return cuestionarioRepository.findAll().stream().map(this::toDTO).toList();
    }

    public CuestionarioDTO obtenerCuestionarioPorId(int id) {
        return cuestionarioRepository.findById(id)
            .map(this::toDTO)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuestionario no encontrado con id: " + id));
    }

    public List<CuestionarioDTO> obtenerCuestionariosPorTutorial(int idTutor) {
        return cuestionarioRepository.findByIdTutor(idTutor).stream().map(this::toDTO).toList();
    }

    public CuestionarioDTO agregarCuestionario(AgregarCuestionario request) {
        Cuestionario c = new Cuestionario();
        c.setTitulo_cuestionario(request.getTitulo_cuestionario());
        c.setDescripcion_cuestionario(request.getDescripcion_cuestionario());
        c.setId_tutor(request.getId_tutor());
        return toDTO(cuestionarioRepository.save(c));
    }

    public CuestionarioDTO actualizarCuestionario(int id, ActualizarCuestionario request) {
        Cuestionario c = cuestionarioRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuestionario no encontrado con id: " + id));
        c.setTitulo_cuestionario(request.getTitulo_cuestionario());
        c.setDescripcion_cuestionario(request.getDescripcion_cuestionario());
        c.setId_tutor(request.getId_tutor());
        return toDTO(cuestionarioRepository.save(c));
    }

    public String eliminarCuestionario(int id) {
        if (!cuestionarioRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuestionario no encontrado con id: " + id);
        }
        cuestionarioRepository.deleteById(id);
        return "Cuestionario con id " + id + " eliminado correctamente";
    }
}
