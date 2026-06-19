package com.orders.relatosdepapel.communications.websocket.service;

import com.orders.relatosdepapel.communications.websocket.client.GeminiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupportResponseService {

    private final GeminiClient geminiClient;

    public String generateResponse(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "Hola, ¿en qué puedo ayudarte con respecto a tus pedidos o libros hoy?";
        }
        return geminiClient.generateResponse(userMessage);
    }
}

