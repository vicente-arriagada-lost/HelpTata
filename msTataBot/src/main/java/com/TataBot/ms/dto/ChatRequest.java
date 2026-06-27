package com.TataBot.ms.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatRequest {
    private String mensaje;
    private List<MensajeHistorial> historial;

    @Data
    public static class MensajeHistorial {
        private String role;
        private String content;
    }
}
