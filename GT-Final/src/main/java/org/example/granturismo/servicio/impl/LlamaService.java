package org.example.granturismo.servicio.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class LlamaService {

    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String API_KEY = "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"; // ⚠️ Puedes mover esto a application.properties si quieres

    public String enviarPrompt(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        // JSON de la solicitud
        Map<String, Object> request = new HashMap<>();
        request.put("model", "meta-llama/llama-4-scout-17b-16e-instruct");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", prompt));
        request.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, entity, Map.class);

            // Extraer contenido del mensaje
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");

        } catch (Exception e) {
            System.out.println("🛑 Error llamando a Groq API: " + e.getMessage());
            return "Lo sentimos, hubo un problema al generar tu recomendación.";
        }
    }
}
