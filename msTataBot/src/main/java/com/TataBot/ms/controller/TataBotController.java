package com.TataBot.ms.controller;

import com.TataBot.ms.dto.ChatRequest;
import com.TataBot.ms.dto.ChatResponse;
import com.TataBot.ms.service.GroqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tatabot")
public class TataBotController {

    @Autowired
    private GroqService groqService;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String respuesta = groqService.chat(request.getMensaje(), request.getHistorial());
        return ResponseEntity.ok(new ChatResponse(respuesta));
    }
}
