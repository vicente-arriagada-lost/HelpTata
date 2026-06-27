package com.Usuario.ms.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.Usuario.ms.models.dto.EmailDTO;
import com.Usuario.ms.models.entities.Email;
import com.Usuario.ms.models.request.ActualizarEmail;
import com.Usuario.ms.models.request.AgregarEmail;
import com.Usuario.ms.repositories.EmailRepository;

//* Servicio que encapsula la lógica de negocio para la gestión de emails
@Service
public class EmailService {

    //* Repositorio inyectado por Spring para acceder a la tabla email
    @Autowired
    private EmailRepository emailRepository;

    //* Convierte una entidad Email a su DTO de respuesta
    private EmailDTO toDTO(Email e) {
        return new EmailDTO(e.getId_email(), e.getEmail());
    }

    //* Retorna todos los emails como DTOs
    public List<EmailDTO> obtenerTodosLosEmail() {
        return emailRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    //* Busca un email por su ID y retorna el DTO
    //! Lanza HTTP 404 si el ID no existe
    public EmailDTO obtenerEmailPorId(int id_email) {
        Email email = emailRepository.findById(id_email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Email no encontrado."));
        return toDTO(email);
    }

    //* Crea un nuevo email y retorna el DTO de respuesta
    public EmailDTO agregarEmail(AgregarEmail nuevoEmail) {
        Email email = new Email();
        email.setEmail(nuevoEmail.getEmail());
        return toDTO(emailRepository.save(email));
    }

    //* Elimina un email por su ID
    //! Lanza HTTP 404 si el ID no existe
    public String eliminarEmail(int id_email) {
        if (emailRepository.existsById(id_email)) {
            emailRepository.deleteById(id_email);
            return "Email eliminado correctamente.";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email no encontrado.");
        }
    }

    //* Actualiza la dirección de email y retorna el DTO de respuesta
    //? id_email viene del path de la URL, no del body del request
    public EmailDTO actualizarEmail(int id_email, ActualizarEmail actEmail) {
        Email email = emailRepository.findById(id_email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Email no encontrado."));
        email.setEmail(actEmail.getEmail());
        return toDTO(emailRepository.save(email));
    }

}
