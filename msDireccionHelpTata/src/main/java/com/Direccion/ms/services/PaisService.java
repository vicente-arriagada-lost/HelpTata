package com.Direccion.ms.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.Direccion.ms.models.dto.PaisDTO;
import com.Direccion.ms.models.entities.Pais;
import com.Direccion.ms.models.request.ActualizarPais;
import com.Direccion.ms.models.request.AgregarPais;
import com.Direccion.ms.repositories.PaisRepository;

//* Servicio que encapsula toda la lógica de negocio relacionada a países
//? @Service marca esta clase para que Spring la detecte e inyecte donde se necesite
@Service
public class PaisService {

    //* Spring inyecta automáticamente el repositorio gracias a @Autowired
    @Autowired
    private PaisRepository paisRepository;

    //* Convierte una entidad Pais a su DTO de respuesta
    private PaisDTO toDTO(Pais p) {
        return new PaisDTO(p.getId_pais(), p.getNombre_pais());
    }

    //* Retorna la lista completa de países como DTOs
    public List<PaisDTO> obtenerTodosLosPaises() {
        return paisRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    //* Busca un país por su ID y retorna el DTO
    //! Lanza HTTP 404 si el ID no existe en la BD
    public PaisDTO obtenerPaisPorId(int id_pais) {
        Pais pais = paisRepository.findById(id_pais)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "País no encontrado."));
        return toDTO(pais);
    }

    //* Crea un nuevo país y retorna el DTO de respuesta
    public PaisDTO agregarPais(AgregarPais nuevoPais) {
        Pais pais = new Pais();
        pais.setNombre_pais(nuevoPais.getNombre_pais());
        return toDTO(paisRepository.save(pais));
    }

    //* Actualiza los datos de un país y retorna el DTO de respuesta
    //? id_pais viene del path de la URL, no del body del request
    public PaisDTO actualizarPais(int id_pais, ActualizarPais actPais) {
        Pais pais = paisRepository.findById(id_pais)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "País no encontrado."));
        pais.setNombre_pais(actPais.getNombre_pais());
        return toDTO(paisRepository.save(pais));
    }

    //* Elimina un país por su ID
    //! Lanza HTTP 404 si el ID no existe
    //! Precaución: si el país tiene regiones asociadas, la BD lanzará un error de FK
    public String eliminarPais(int id_pais) {
        if (paisRepository.existsById(id_pais)) {
            paisRepository.deleteById(id_pais);
            return "País eliminado correctamente.";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "País no encontrado.");
        }
    }

}
