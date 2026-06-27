package com.Tutoriales.ms.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.Tutoriales.ms.clients.LogClient;
import com.Tutoriales.ms.models.dto.FotoDTO;
import com.Tutoriales.ms.models.entities.Foto;
import com.Tutoriales.ms.models.entities.Tutorial;
import com.Tutoriales.ms.models.request.ActualizarFoto;
import com.Tutoriales.ms.models.request.AgregarFoto;
import com.Tutoriales.ms.repositories.FotoRepository;
import com.Tutoriales.ms.repositories.TutorialRepository;

//* Servicio que encapsula toda la lógica de negocio relacionada a fotos de tutoriales
@Service
public class FotoService {

    @Autowired
    private FotoRepository fotoRepository;

    @Autowired
    private TutorialRepository tutorialRepository;

    @Autowired
    private LogClient logClient;

    //* Convierte una entidad Foto a su DTO de respuesta
    private FotoDTO toDTO(Foto f) {
        return new FotoDTO(f.getId_foto(), f.getFoto(), f.getTutorial().getId_tutor());
    }

    //* Retorna la lista completa de fotos como DTOs
    public List<FotoDTO> obtenerTodasLasFotos() {
        return fotoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    //* Retorna todas las fotos de un tutorial específico como DTOs
    public List<FotoDTO> obtenerFotosPorTutorial(int idTutor) {
        return fotoRepository.findByTutorial_IdTutor(idTutor)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    //* Busca una foto por su ID y retorna el DTO
    //! Lanza HTTP 404 si el ID no existe en la BD
    public FotoDTO obtenerFotoPorId(int id_foto) {
        Foto foto = fotoRepository.findById(id_foto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Foto no encontrada."));
        return toDTO(foto);
    }

    //* Crea una nueva foto y retorna el DTO de respuesta
    //! Lanza HTTP 404 si el tutorial referenciado no existe
    public FotoDTO agregarFoto(AgregarFoto nuevaFoto) {
        Tutorial tutorial = tutorialRepository.findById(nuevaFoto.getId_tutor())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutorial no encontrado."));
        Foto foto = new Foto();
        foto.setFoto(nuevaFoto.getFoto());
        foto.setTutorial(tutorial);
        FotoDTO resultado = toDTO(fotoRepository.save(foto));
        logClient.registrar("INFO",
                "Foto agregada al tutorial id=" + nuevaFoto.getId_tutor()
                        + " (foto id=" + resultado.id_foto() + ")",
                null, null);
        return resultado;
    }

    //* Actualiza el contenido y/o el tutorial de una foto y retorna el DTO de respuesta
    public FotoDTO actualizarFoto(int id_foto, ActualizarFoto actFoto) {
        Foto foto = fotoRepository.findById(id_foto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Foto no encontrada."));
        Tutorial tutorial = tutorialRepository.findById(actFoto.getId_tutor())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutorial no encontrado."));
        foto.setFoto(actFoto.getFoto());
        foto.setTutorial(tutorial);
        return toDTO(fotoRepository.save(foto));
    }

    //* Elimina una foto por su ID
    //! Lanza HTTP 404 si el ID no existe
    public String eliminarFoto(int id_foto) {
        if (fotoRepository.existsById(id_foto)) {
            fotoRepository.deleteById(id_foto);
            logClient.registrar("INFO",
                    "Foto eliminada: id=" + id_foto,
                    null, null);
            return "Foto eliminada correctamente.";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Foto no encontrada.");
        }
    }
}
