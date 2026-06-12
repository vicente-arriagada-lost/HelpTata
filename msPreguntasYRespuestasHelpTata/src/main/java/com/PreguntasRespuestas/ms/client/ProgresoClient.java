package com.PreguntasRespuestas.ms.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class ProgresoClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ms.progreso.url}")
    private String progresoUrl;

    // Busca el progreso del usuario en el tutorial y actualiza preguntas acertadas/falladas.
    // Si no existe registro de progreso, omite la actualización sin lanzar error.
    public void actualizarProgreso(int idUsuario, int idTutorial, int preguntasAcertadas, int preguntasFalladas) {
        try {
            String urlBuscar = progresoUrl + "/api/progreso/usuario/" + idUsuario + "/tutorial/" + idTutorial;
            Map<?, ?> progreso = restTemplate.getForObject(urlBuscar, Map.class);

            if (progreso == null) return;

            int idProgreso = (int) progreso.get("id_progreso");
            int acertadasActuales = (int) progreso.get("preguntas_acertadas");
            int falladasActuales = (int) progreso.get("preguntas_falladas");

            Map<String, Object> body = new java.util.HashMap<>((Map<String, Object>) progreso);
            body.put("preguntas_acertadas", acertadasActuales + preguntasAcertadas);
            body.put("preguntas_falladas", falladasActuales + preguntasFalladas);

            String urlActualizar = progresoUrl + "/api/progreso/" + idProgreso;
            restTemplate.put(urlActualizar, body);

        } catch (HttpClientErrorException.NotFound e) {
            // No hay registro de progreso aún para este usuario y tutorial: se ignora
        } catch (Exception e) {
            // ms-Progreso no disponible: se registra en consola pero no se interrumpe el flujo
            System.err.println("[ProgresoClient] No se pudo actualizar progreso: " + e.getMessage());
        }
    }
}
