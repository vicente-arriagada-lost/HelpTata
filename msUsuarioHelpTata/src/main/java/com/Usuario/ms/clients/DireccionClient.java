package com.Usuario.ms.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

//* Cliente REST que comunica el MS Usuario con el MS Direccion (puerto 8084)
//* Patrón de comunicación: sincrónico via HTTP — el MS Usuario llama al MS Direccion directamente
//* Responsabilidad: validar que una dirección existe antes de asignarla a un usuario
//? @Component marca esta clase para que Spring la detecte e inyecte como un bean
@Component
public class DireccionClient {

    //* RestTemplate es el cliente HTTP de Spring para hacer llamadas REST sincrónicas
    private final RestTemplate restTemplate;

    //* URL base del MS Direccion, configurable desde application.properties
    //? Si la propiedad no existe, usa "http://localhost:8084" como valor por defecto
    @Value("${ms.direccion.url:http://localhost:8084}")
    private String direccionUrl;

    public DireccionClient() {
        this.restTemplate = new RestTemplate();
    }

    //* Verifica que una dirección existe en el MS Direccion antes de asignarla a un usuario
    //? Llama a GET http://localhost:8084/api/direcciones/{idDireccion}
    //! Lanza HTTP 404 si la dirección no existe en el MS Direccion
    //! Lanza HTTP 502 Bad Gateway si el MS Direccion no está disponible
    public void validarDireccion(int idDireccion) {
        String url = direccionUrl + "/api/direcciones/" + idDireccion;
        try {
            restTemplate.getForObject(url, Object.class);
        } catch (HttpClientErrorException e) {
            //* Si el MS Direccion responde 404, la dirección no existe — se propaga como error al cliente
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "La dirección con id " + idDireccion + " no existe en el MS Direccion.");
            }
            //* Cualquier otro error HTTP se traduce a 502 para indicar fallo en comunicación entre MSs
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Error al comunicarse con el MS Direccion.");
        }
    }

}
