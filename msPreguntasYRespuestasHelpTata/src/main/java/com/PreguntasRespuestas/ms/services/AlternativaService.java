package com.PreguntasRespuestas.ms.services;

import com.PreguntasRespuestas.ms.models.dto.AlternativaDTO;
import com.PreguntasRespuestas.ms.models.entities.Alternativa;
import com.PreguntasRespuestas.ms.models.request.ActualizarAlternativa;
import com.PreguntasRespuestas.ms.models.request.AgregarAlternativa;
import com.PreguntasRespuestas.ms.repositories.AlternativaRepository;
import com.PreguntasRespuestas.ms.repositories.PreguntaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AlternativaService {

    @Autowired
    private AlternativaRepository alternativaRepository;

    @Autowired
    private PreguntaRepository preguntaRepository;

    private AlternativaDTO toDTO(Alternativa a) {
        return new AlternativaDTO(
            a.getId_alternativa(),
            a.getTexto_alternativa(),
            a.isEs_correcta(),
            a.getId_pregunta()
        );
    }

    public List<AlternativaDTO> obtenerTodasLasAlternativas() {
        return alternativaRepository.findAll().stream().map(this::toDTO).toList();
    }

    public AlternativaDTO obtenerAlternativaPorId(int id) {
        return alternativaRepository.findById(id)
            .map(this::toDTO)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alternativa no encontrada con id: " + id));
    }

    public List<AlternativaDTO> obtenerAlternativasPorPregunta(int idPregunta) {
        if (!preguntaRepository.existsById(idPregunta)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada con id: " + idPregunta);
        }
        return alternativaRepository.findByIdPregunta(idPregunta).stream().map(this::toDTO).toList();
    }

    public AlternativaDTO agregarAlternativa(AgregarAlternativa request) {
        if (!preguntaRepository.existsById(request.getId_pregunta())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada con id: " + request.getId_pregunta());
        }
        Alternativa a = new Alternativa();
        a.setTexto_alternativa(request.getTexto_alternativa());
        a.setEs_correcta(request.getEs_correcta());
        a.setId_pregunta(request.getId_pregunta());
        return toDTO(alternativaRepository.save(a));
    }

    public AlternativaDTO actualizarAlternativa(int id, ActualizarAlternativa request) {
        Alternativa a = alternativaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alternativa no encontrada con id: " + id));
        if (!preguntaRepository.existsById(request.getId_pregunta())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada con id: " + request.getId_pregunta());
        }
        a.setTexto_alternativa(request.getTexto_alternativa());
        a.setEs_correcta(request.getEs_correcta());
        a.setId_pregunta(request.getId_pregunta());
        return toDTO(alternativaRepository.save(a));
    }

    public String eliminarAlternativa(int id) {
        if (!alternativaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Alternativa no encontrada con id: " + id);
        }
        alternativaRepository.deleteById(id);
        return "Alternativa con id " + id + " eliminada correctamente";
    }
}
