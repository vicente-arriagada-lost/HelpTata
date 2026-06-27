package com.Logs.ms.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.Logs.ms.models.dto.LogDTO;
import com.Logs.ms.models.entities.Log;
import com.Logs.ms.models.request.ActualizarLog;
import com.Logs.ms.models.request.AgregarLog;
import com.Logs.ms.repositories.LogRepository;

//* Servicio que encapsula toda la lógica de negocio relacionada a los logs del sistema
//? @Service marca esta clase para que Spring la detecte e inyecte donde se necesite
@Service
public class LogService {

    //* Spring inyecta automáticamente el repositorio gracias a @Autowired
    @Autowired
    private LogRepository logRepository;

    //* Convierte una entidad Log a su DTO de respuesta
    private LogDTO toDTO(Log l) {
        return new LogDTO(
                l.getId_log(),
                l.getTipo_log(),
                l.getServicio_origen(),
                l.getMensaje_log(),
                l.getFecha_hora_log(),
                l.getId_usuario(),
                l.getIp_log(),
                l.getDetalle_log()
        );
    }

    //* Retorna la lista completa de logs como DTOs
    public List<LogDTO> obtenerTodosLosLogs() {
        return logRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    //* Busca un log por su ID y retorna el DTO
    //! Lanza HTTP 404 si el ID no existe en la BD
    public LogDTO obtenerLogPorId(int id_log) {
        Log log = logRepository.findById(id_log)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Log no encontrado."));
        return toDTO(log);
    }

    //* Retorna todos los logs que coincidan con el tipo indicado como DTOs (ej: "ERROR")
    public List<LogDTO> obtenerLogsPorTipo(String tipo) {
        return logRepository.findByTipoLog(tipo)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    //* Retorna todos los logs asociados a un usuario específico como DTOs
    public List<LogDTO> obtenerLogsPorUsuario(Integer idUsuario) {
        return logRepository.findByIdUsuario(idUsuario)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    //* Retorna todos los logs de un microservicio específico como DTOs
    public List<LogDTO> obtenerLogsPorServicio(String servicio) {
        return logRepository.findByServicioOrigen(servicio)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    //* Crea un nuevo log y retorna el DTO de respuesta
    //! fecha_hora_log se asigna aquí con LocalDateTime.now() — no viene del request
    public LogDTO agregarLog(AgregarLog nuevoLog) {
        Log log = new Log();
        log.setTipo_log(nuevoLog.getTipo_log());
        log.setServicio_origen(nuevoLog.getServicio_origen());
        log.setMensaje_log(nuevoLog.getMensaje_log());
        log.setId_usuario(nuevoLog.getId_usuario());
        log.setIp_log(nuevoLog.getIp_log());
        log.setDetalle_log(nuevoLog.getDetalle_log());
        //* Se asigna la fecha y hora actuales como timestamp del evento
        log.setFecha_hora_log(LocalDateTime.now());
        return toDTO(logRepository.save(log));
    }

    //* Actualiza los datos de un log y retorna el DTO de respuesta
    //? id_log viene del path de la URL, no del body del request
    //? fecha_hora_log no se modifica — conserva el timestamp original del evento
    public LogDTO actualizarLog(int id_log, ActualizarLog actLog) {
        Log log = logRepository.findById(id_log)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Log no encontrado."));
        log.setTipo_log(actLog.getTipo_log());
        log.setServicio_origen(actLog.getServicio_origen());
        log.setMensaje_log(actLog.getMensaje_log());
        log.setId_usuario(actLog.getId_usuario());
        log.setIp_log(actLog.getIp_log());
        log.setDetalle_log(actLog.getDetalle_log());
        //? fecha_hora_log no se modifica — se conserva el timestamp original
        return toDTO(logRepository.save(log));
    }

    //* Elimina un log por su ID
    //! Lanza HTTP 404 si el ID no existe
    public String eliminarLog(int id_log) {
        if (logRepository.existsById(id_log)) {
            logRepository.deleteById(id_log);
            return "Log eliminado correctamente.";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Log no encontrado.");
        }
    }

}
