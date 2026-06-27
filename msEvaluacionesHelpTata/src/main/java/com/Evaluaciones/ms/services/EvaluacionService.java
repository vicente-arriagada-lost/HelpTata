package com.Evaluaciones.ms.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.Evaluaciones.ms.clients.LogClient;
import com.Evaluaciones.ms.models.dto.EvaluacionDTO;
import com.Evaluaciones.ms.models.entities.Evaluacion;
import com.Evaluaciones.ms.models.request.ActualizarEvaluacion;
import com.Evaluaciones.ms.models.request.AgregarEvaluacion;
import com.Evaluaciones.ms.repositories.EvaluacionRepository;

//* Servicio que encapsula toda la lógica de negocio relacionada a evaluaciones
@Service
public class EvaluacionService {

    @Autowired
    private EvaluacionRepository evaluacionRepository;

    @Autowired
    private LogClient logClient;

    //* Convierte una entidad Evaluacion a su DTO de respuesta
    private EvaluacionDTO toDTO(Evaluacion e) {
        return new EvaluacionDTO(
                e.getId_eva(),
                e.getNombre_eva(),
                e.getTipo_eva(),
                e.getNivel_eva(),
                e.getBanco_preg(),
                e.getId_tutor()
        );
    }

    //* Retorna la lista completa de evaluaciones como DTOs
    public List<EvaluacionDTO> obtenerTodasLasEvaluaciones() {
        return evaluacionRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    //* Busca una evaluación por su ID y retorna el DTO
    //! Lanza HTTP 404 si el ID no existe en la BD
    public EvaluacionDTO obtenerEvaluacionPorId(int id_eva) {
        Evaluacion evaluacion = evaluacionRepository.findById(id_eva)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evaluación no encontrada."));
        return toDTO(evaluacion);
    }

    //* Retorna todas las evaluaciones de un tutorial dado como DTOs
    public List<EvaluacionDTO> obtenerEvaluacionesPorTutorial(int idTutor) {
        return evaluacionRepository.findByIdTutor(idTutor)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    //* Retorna todas las evaluaciones de un nivel específico como DTOs
    public List<EvaluacionDTO> obtenerEvaluacionesPorNivel(String nivelEva) {
        return evaluacionRepository.findByNivel_eva(nivelEva)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    //* Retorna todas las evaluaciones de un tipo específico como DTOs
    public List<EvaluacionDTO> obtenerEvaluacionesPorTipo(String tipoEva) {
        return evaluacionRepository.findByTipo_eva(tipoEva)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    //* Crea una nueva evaluación y retorna el DTO de respuesta
    public EvaluacionDTO agregarEvaluacion(AgregarEvaluacion nuevaEvaluacion) {
        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setNombre_eva(nuevaEvaluacion.getNombre_eva());
        evaluacion.setTipo_eva(nuevaEvaluacion.getTipo_eva());
        evaluacion.setNivel_eva(nuevaEvaluacion.getNivel_eva());
        evaluacion.setBanco_preg(nuevaEvaluacion.getBanco_preg());
        evaluacion.setId_tutor(nuevaEvaluacion.getId_tutor());
        EvaluacionDTO resultado = toDTO(evaluacionRepository.save(evaluacion));
        logClient.registrar("INFO",
                "Evaluación creada: '" + nuevaEvaluacion.getNombre_eva()
                        + "' (id=" + resultado.id_eva() + ", tipo=" + nuevaEvaluacion.getTipo_eva()
                        + ", nivel=" + nuevaEvaluacion.getNivel_eva() + ", tutorial=" + nuevaEvaluacion.getId_tutor() + ")",
                null, null);
        return resultado;
    }

    //* Actualiza los datos de una evaluación y retorna el DTO de respuesta
    public EvaluacionDTO actualizarEvaluacion(int id_eva, ActualizarEvaluacion actEvaluacion) {
        Evaluacion evaluacion = evaluacionRepository.findById(id_eva)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evaluación no encontrada."));
        evaluacion.setNombre_eva(actEvaluacion.getNombre_eva());
        evaluacion.setTipo_eva(actEvaluacion.getTipo_eva());
        evaluacion.setNivel_eva(actEvaluacion.getNivel_eva());
        evaluacion.setBanco_preg(actEvaluacion.getBanco_preg());
        evaluacion.setId_tutor(actEvaluacion.getId_tutor());
        EvaluacionDTO resultado = toDTO(evaluacionRepository.save(evaluacion));
        logClient.registrar("INFO",
                "Evaluación actualizada: id=" + id_eva + " → '" + actEvaluacion.getNombre_eva() + "'",
                null, null);
        return resultado;
    }

    //* Elimina una evaluación por su ID
    //! Lanza HTTP 404 si el ID no existe
    public String eliminarEvaluacion(int id_eva) {
        if (evaluacionRepository.existsById(id_eva)) {
            evaluacionRepository.deleteById(id_eva);
            logClient.registrar("INFO",
                    "Evaluación eliminada: id=" + id_eva,
                    null, null);
            return "Evaluación eliminada correctamente.";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evaluación no encontrada.");
        }
    }
}
