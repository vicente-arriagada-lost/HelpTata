package com.Usuario.ms.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.Usuario.ms.models.dto.RolDTO;
import com.Usuario.ms.models.entities.Rol;
import com.Usuario.ms.models.request.ActualizarRol;
import com.Usuario.ms.models.request.AgregarRol;
import com.Usuario.ms.repositories.RolRepository;

//* Servicio que encapsula la lógica de negocio para la gestión de roles de usuario
@Service
public class RolUsuarioService {

    //* Repositorio inyectado por Spring para acceder a la tabla rol
    @Autowired
    private RolRepository rolRepository;

    //* Convierte una entidad Rol a su DTO de respuesta
    private RolDTO toDTO(Rol r) {
        return new RolDTO(r.getId_rol(), r.getTipo_rol());
    }

    //* Retorna todos los roles como DTOs
    public List<RolDTO> obtenerTodosLosRoles() {
        return rolRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    //* Busca un rol por su ID y retorna el DTO
    //! Lanza HTTP 404 si el ID no existe
    public RolDTO obtenerRolPorId(int id_rol) {
        Rol rol = rolRepository.findById(id_rol)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado."));
        return toDTO(rol);
    }

    //* Crea un nuevo rol y retorna el DTO de respuesta
    public RolDTO agregarRol(AgregarRol nuevoRol) {
        Rol rol = new Rol();
        rol.setTipo_rol(nuevoRol.getTipo_rol());
        return toDTO(rolRepository.save(rol));
    }

    //* Elimina un rol por su ID
    //! Lanza HTTP 404 si el ID no existe
    public String eliminarRol(int id_rol) {
        if (rolRepository.existsById(id_rol)) {
            rolRepository.deleteById(id_rol);
            return "Rol eliminado correctamente.";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado.");
        }
    }

    //* Actualiza el tipo de un rol existente y retorna el DTO de respuesta
    //? id_rol viene del path de la URL, no del body del request
    public RolDTO actualizarRol(int id_rol, ActualizarRol actRol) {
        Rol rol = rolRepository.findById(id_rol)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado."));
        rol.setTipo_rol(actRol.getTipo_rol());
        return toDTO(rolRepository.save(rol));
    }

}
