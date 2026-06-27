package com.Tutoriales.ms.services;

import com.Tutoriales.ms.models.entities.Configuracion;
import com.Tutoriales.ms.repositories.ConfiguracionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

//* Servicio para leer configuraciones globales almacenadas en la tabla "configuracion"
@Service
public class ConfiguracionService {

    @Autowired
    private ConfiguracionRepository configuracionRepository;

    //* Retorna el valor de una configuración por su clave
    //! Lanza HTTP 404 si la clave no existe en la BD
    public String obtenerValor(String clave) {
        return configuracionRepository.findByClaveConfig(clave)
                .map(Configuracion::getValorConfig)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Configuración no encontrada: " + clave));
    }
}
