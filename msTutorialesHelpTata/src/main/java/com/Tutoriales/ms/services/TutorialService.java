package com.Tutoriales.ms.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.Tutoriales.ms.clients.LogClient;
import com.Tutoriales.ms.models.dto.TutorialDTO;
import com.Tutoriales.ms.models.entities.Tutorial;
import com.Tutoriales.ms.models.request.ActualizarTutorial;
import com.Tutoriales.ms.models.request.AgregarTutorial;
import com.Tutoriales.ms.repositories.TutorialRepository;

//* Servicio que encapsula toda la lógica de negocio relacionada a tutoriales
@Service
public class TutorialService {

    @Autowired
    private TutorialRepository tutorialRepository;

    @Autowired
    private LogClient logClient;

    //* Convierte una entidad Tutorial a su DTO de respuesta
    private TutorialDTO toDTO(Tutorial t) {
        return new TutorialDTO(
                t.getId_tutor(),
                t.getNombre_tuto(),
                t.getCat_tuto(),
                t.getNivel_tuto(),
                t.getTutorial(),
                t.getTiempo_tutorial(),
                t.getDescripcion_tuto()
        );
    }

    //* Retorna la lista completa de tutoriales como DTOs
    public List<TutorialDTO> obtenerTodosLosTutoriales() {
        return tutorialRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    //* Busca un tutorial por su ID y retorna el DTO
    //! Lanza HTTP 404 si el ID no existe en la BD
    public TutorialDTO obtenerTutorialPorId(int id_tutor) {
        Tutorial tutorial = tutorialRepository.findById(id_tutor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutorial no encontrado."));
        return toDTO(tutorial);
    }

    //* Retorna todos los tutoriales de una categoría específica como DTOs
    public List<TutorialDTO> obtenerTutorialesPorCategoria(String categoria) {
        return tutorialRepository.findByCategoria(categoria)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    //* Retorna todos los tutoriales de un nivel específico como DTOs
    public List<TutorialDTO> obtenerTutorialesPorNivel(String nivel) {
        return tutorialRepository.findByNivel(nivel)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    //* Crea un nuevo tutorial y retorna el DTO de respuesta
    public TutorialDTO agregarTutorial(AgregarTutorial nuevoTutorial) {
        Tutorial tutorial = new Tutorial();
        tutorial.setNombre_tuto(nuevoTutorial.getNombre_tuto());
        tutorial.setCat_tuto(nuevoTutorial.getCat_tuto());
        tutorial.setNivel_tuto(nuevoTutorial.getNivel_tuto());
        tutorial.setTutorial(nuevoTutorial.getTutorial());
        tutorial.setTiempo_tutorial(nuevoTutorial.getTiempo_tutorial());
        tutorial.setDescripcion_tuto(nuevoTutorial.getDescripcion_tuto());
        TutorialDTO resultado = toDTO(tutorialRepository.save(tutorial));
        logClient.registrar("INFO",
                "Tutorial creado: '" + nuevoTutorial.getNombre_tuto()
                        + "' (id=" + resultado.id_tutor() + ", categoría=" + nuevoTutorial.getCat_tuto()
                        + ", nivel=" + nuevoTutorial.getNivel_tuto() + ")",
                null, null);
        return resultado;
    }

    //* Actualiza los datos de un tutorial y retorna el DTO de respuesta
    public TutorialDTO actualizarTutorial(int id_tutor, ActualizarTutorial actTutorial) {
        Tutorial tutorial = tutorialRepository.findById(id_tutor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutorial no encontrado."));
        tutorial.setNombre_tuto(actTutorial.getNombre_tuto());
        tutorial.setCat_tuto(actTutorial.getCat_tuto());
        tutorial.setNivel_tuto(actTutorial.getNivel_tuto());
        tutorial.setTutorial(actTutorial.getTutorial());
        tutorial.setTiempo_tutorial(actTutorial.getTiempo_tutorial());
        tutorial.setDescripcion_tuto(actTutorial.getDescripcion_tuto());
        TutorialDTO resultado = toDTO(tutorialRepository.save(tutorial));
        logClient.registrar("INFO",
                "Tutorial actualizado: id=" + id_tutor + " → '" + actTutorial.getNombre_tuto() + "'",
                null, null);
        return resultado;
    }

    //* Elimina un tutorial por su ID
    //! Lanza HTTP 404 si el ID no existe
    public String eliminarTutorial(int id_tutor) {
        if (tutorialRepository.existsById(id_tutor)) {
            tutorialRepository.deleteById(id_tutor);
            logClient.registrar("INFO",
                    "Tutorial eliminado: id=" + id_tutor,
                    null, null);
            return "Tutorial eliminado correctamente.";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutorial no encontrado.");
        }
    }
}
