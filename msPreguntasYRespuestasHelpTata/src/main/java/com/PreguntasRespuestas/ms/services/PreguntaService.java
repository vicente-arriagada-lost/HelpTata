package com.PreguntasRespuestas.ms.services;

import com.PreguntasRespuestas.ms.models.dto.PreguntaDTO;
import com.PreguntasRespuestas.ms.models.entities.Pregunta;
import com.PreguntasRespuestas.ms.models.request.ActualizarPregunta;
import com.PreguntasRespuestas.ms.models.request.AgregarPregunta;
import com.PreguntasRespuestas.ms.repositories.CuestionarioRepository;
import com.PreguntasRespuestas.ms.repositories.PreguntaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PreguntaService {

    @Autowired
    private PreguntaRepository preguntaRepository;

    @Autowired
    private CuestionarioRepository cuestionarioRepository;

    private PreguntaDTO toDTO(Pregunta p) {
        return new PreguntaDTO(
            p.getId_pregunta(),
            p.getEnunciado_pregunta(),
            p.getId_cuestionario()
        );
    }

    public List<PreguntaDTO> obtenerTodasLasPreguntas() {
        return preguntaRepository.findAll().stream().map(this::toDTO).toList();
    }

    public PreguntaDTO obtenerPreguntaPorId(int id) {
        return preguntaRepository.findById(id)
            .map(this::toDTO)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada con id: " + id));
    }

    public List<PreguntaDTO> obtenerPreguntasPorCuestionario(int idCuestionario) {
        if (!cuestionarioRepository.existsById(idCuestionario)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuestionario no encontrado con id: " + idCuestionario);
        }
        return preguntaRepository.findByIdCuestionario(idCuestionario).stream().map(this::toDTO).toList();
    }

    public PreguntaDTO agregarPregunta(AgregarPregunta request) {
        if (!cuestionarioRepository.existsById(request.getId_cuestionario())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuestionario no encontrado con id: " + request.getId_cuestionario());
        }
        Pregunta p = new Pregunta();
        p.setEnunciado_pregunta(request.getEnunciado_pregunta());
        p.setId_cuestionario(request.getId_cuestionario());
        return toDTO(preguntaRepository.save(p));
    }

    public PreguntaDTO actualizarPregunta(int id, ActualizarPregunta request) {
        Pregunta p = preguntaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada con id: " + id));
        if (!cuestionarioRepository.existsById(request.getId_cuestionario())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuestionario no encontrado con id: " + request.getId_cuestionario());
        }
        p.setEnunciado_pregunta(request.getEnunciado_pregunta());
        p.setId_cuestionario(request.getId_cuestionario());
        return toDTO(preguntaRepository.save(p));
    }

    public String eliminarPregunta(int id) {
        if (!preguntaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada con id: " + id);
        }
        preguntaRepository.deleteById(id);
        return "Pregunta con id " + id + " eliminada correctamente";
    }
}
