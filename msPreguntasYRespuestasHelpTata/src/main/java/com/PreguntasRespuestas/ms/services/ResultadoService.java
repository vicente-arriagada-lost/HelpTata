package com.PreguntasRespuestas.ms.services;

import com.PreguntasRespuestas.ms.client.ProgresoClient;
import com.PreguntasRespuestas.ms.models.dto.ResultadoCuestionarioDTO;
import com.PreguntasRespuestas.ms.models.dto.ResultadoSubmisionDTO;
import com.PreguntasRespuestas.ms.models.entities.Alternativa;
import com.PreguntasRespuestas.ms.models.entities.Cuestionario;
import com.PreguntasRespuestas.ms.models.entities.ResultadoCuestionario;
import com.PreguntasRespuestas.ms.models.entities.RespuestaUsuario;
import com.PreguntasRespuestas.ms.models.request.SubmitirCuestionario;
import com.PreguntasRespuestas.ms.repositories.AlternativaRepository;
import com.PreguntasRespuestas.ms.repositories.CuestionarioRepository;
import com.PreguntasRespuestas.ms.repositories.ResultadoCuestionarioRepository;
import com.PreguntasRespuestas.ms.repositories.RespuestaUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResultadoService {

    @Autowired
    private ResultadoCuestionarioRepository resultadoRepository;

    @Autowired
    private RespuestaUsuarioRepository respuestaUsuarioRepository;

    @Autowired
    private CuestionarioRepository cuestionarioRepository;

    @Autowired
    private AlternativaRepository alternativaRepository;

    @Autowired
    private ProgresoClient progresoClient;

    private ResultadoCuestionarioDTO toDTO(ResultadoCuestionario r) {
        return new ResultadoCuestionarioDTO(
            r.getId_resultado(),
            r.getId_usuario(),
            r.getId_cuestionario(),
            r.getCorrectas(),
            r.getIncorrectas(),
            r.getPorcentaje(),
            r.getFecha_resultado()
        );
    }

    public List<ResultadoCuestionarioDTO> obtenerTodosLosResultados() {
        return resultadoRepository.findAll().stream().map(this::toDTO).toList();
    }

    public ResultadoCuestionarioDTO obtenerResultadoPorId(int id) {
        return resultadoRepository.findById(id)
            .map(this::toDTO)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resultado no encontrado con id: " + id));
    }

    public List<ResultadoCuestionarioDTO> obtenerResultadosPorUsuario(int idUsuario) {
        return resultadoRepository.findByIdUsuario(idUsuario).stream().map(this::toDTO).toList();
    }

    public List<ResultadoCuestionarioDTO> obtenerResultadosPorCuestionario(int idCuestionario) {
        return resultadoRepository.findByIdCuestionario(idCuestionario).stream().map(this::toDTO).toList();
    }

    public List<ResultadoCuestionarioDTO> obtenerHistorialUsuarioEnCuestionario(int idUsuario, int idCuestionario) {
        return resultadoRepository.findByIdUsuarioAndIdCuestionario(idUsuario, idCuestionario).stream().map(this::toDTO).toList();
    }

    // Flujo principal: recibe respuestas, corrige, guarda y notifica progreso
    public ResultadoSubmisionDTO submitirCuestionario(int idCuestionario, SubmitirCuestionario request) {
        Cuestionario cuestionario = cuestionarioRepository.findById(idCuestionario)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuestionario no encontrado con id: " + idCuestionario));

        int correctas = 0;
        int incorrectas = 0;

        for (SubmitirCuestionario.RespuestaItem item : request.getRespuestas()) {
            Alternativa alternativa = alternativaRepository.findById(item.getId_alternativa_seleccionada())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Alternativa no encontrada con id: " + item.getId_alternativa_seleccionada()));

            if (alternativa.getId_pregunta() != item.getId_pregunta()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La alternativa " + item.getId_alternativa_seleccionada() + " no corresponde a la pregunta " + item.getId_pregunta());
            }

            if (alternativa.isEs_correcta()) {
                correctas++;
            } else {
                incorrectas++;
            }
        }

        int total = correctas + incorrectas;
        double porcentaje = total > 0 ? Math.round((correctas * 100.0 / total) * 10.0) / 10.0 : 0.0;

        ResultadoCuestionario resultado = new ResultadoCuestionario();
        resultado.setId_usuario(request.getId_usuario());
        resultado.setId_cuestionario(idCuestionario);
        resultado.setCorrectas(correctas);
        resultado.setIncorrectas(incorrectas);
        resultado.setPorcentaje(porcentaje);
        resultado.setFecha_resultado(LocalDateTime.now());
        ResultadoCuestionario guardado = resultadoRepository.save(resultado);

        for (SubmitirCuestionario.RespuestaItem item : request.getRespuestas()) {
            RespuestaUsuario respuesta = new RespuestaUsuario();
            respuesta.setId_usuario(request.getId_usuario());
            respuesta.setId_pregunta(item.getId_pregunta());
            respuesta.setId_alternativa_seleccionada(item.getId_alternativa_seleccionada());
            respuesta.setId_resultado(guardado.getId_resultado());
            respuestaUsuarioRepository.save(respuesta);
        }

        // Notificar a ms-Progreso para actualizar preguntas acertadas/falladas
        progresoClient.actualizarProgreso(request.getId_usuario(), cuestionario.getId_tutor(), correctas, incorrectas);

        return new ResultadoSubmisionDTO(guardado.getId_resultado(), correctas, incorrectas, porcentaje);
    }

    public String eliminarResultado(int id) {
        if (!resultadoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resultado no encontrado con id: " + id);
        }
        resultadoRepository.deleteById(id);
        return "Resultado con id " + id + " eliminado correctamente";
    }
}
