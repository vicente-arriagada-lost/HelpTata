package com.Direccion.ms.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.Direccion.ms.models.dto.ComunaDTO;
import com.Direccion.ms.models.entities.Ciudad;
import com.Direccion.ms.models.entities.Comuna;
import com.Direccion.ms.models.request.ActualizarComuna;
import com.Direccion.ms.models.request.AgregarComuna;
import com.Direccion.ms.repositories.CiudadRepository;
import com.Direccion.ms.repositories.ComunaRepository;

//* Servicio que encapsula toda la lógica de negocio relacionada a comunas
//? @Service marca esta clase para que Spring la detecte e inyecte donde se necesite
@Service
public class ComunaService {

    //* Spring inyecta automáticamente los repositorios gracias a @Autowired
    @Autowired
    private ComunaRepository comunaRepository;

    //* Se necesita CiudadRepository para validar que la ciudad existe antes de crear/actualizar
    @Autowired
    private CiudadRepository ciudadRepository;

    //* Convierte una entidad Comuna a su DTO de respuesta
    // En vez de incluir el objeto Ciudad completo, solo se expone su ID (id_ciudad)
    private ComunaDTO toDTO(Comuna c) {
        return new ComunaDTO(c.getId_comuna(), c.getNombre_comuna(), c.getCiudad().getId_ciudad());
    }

    //* Retorna la lista completa de comunas como DTOs
    public List<ComunaDTO> obtenerTodasLasComunas() {
        return comunaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    //* Retorna todas las comunas que pertenecen a una ciudad dada como DTOs
    //? Útil para poblar selects en cascada: al seleccionar una ciudad, se cargan sus comunas
    public List<ComunaDTO> obtenerComunasPorCiudad(int idCiudad) {
        return comunaRepository.findByCiudad_IdCiudad(idCiudad)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    //* Busca una comuna por su ID y retorna el DTO
    //! Lanza HTTP 404 si el ID no existe en la BD
    public ComunaDTO obtenerComunaPorId(int id_comuna) {
        Comuna comuna = comunaRepository.findById(id_comuna)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comuna no encontrada."));
        return toDTO(comuna);
    }

    //* Crea una nueva comuna y retorna el DTO de respuesta
    //! Lanza HTTP 404 si la ciudad referenciada (id_ciudad) no existe — integridad garantizada
    public ComunaDTO agregarComuna(AgregarComuna nuevaComuna) {
        Ciudad ciudad = ciudadRepository.findById(nuevaComuna.getId_ciudad())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ciudad no encontrada."));
        Comuna comuna = new Comuna();
        comuna.setNombre_comuna(nuevaComuna.getNombre_comuna());
        //* Se asigna el objeto Ciudad completo para que JPA gestione la FK
        comuna.setCiudad(ciudad);
        return toDTO(comunaRepository.save(comuna));
    }

    //* Actualiza los datos de una comuna y retorna el DTO de respuesta
    //? id_comuna viene del path de la URL, no del body del request
    public ComunaDTO actualizarComuna(int id_comuna, ActualizarComuna actComuna) {
        Comuna comuna = comunaRepository.findById(id_comuna)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comuna no encontrada."));
        Ciudad ciudad = ciudadRepository.findById(actComuna.getId_ciudad())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ciudad no encontrada."));
        comuna.setNombre_comuna(actComuna.getNombre_comuna());
        comuna.setCiudad(ciudad);
        return toDTO(comunaRepository.save(comuna));
    }

    //* Elimina una comuna por su ID
    //! Lanza HTTP 404 si el ID no existe
    //! Precaución: si la comuna tiene direcciones asociadas, la BD lanzará un error de FK
    public String eliminarComuna(int id_comuna) {
        if (comunaRepository.existsById(id_comuna)) {
            comunaRepository.deleteById(id_comuna);
            return "Comuna eliminada correctamente.";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comuna no encontrada.");
        }
    }

}
