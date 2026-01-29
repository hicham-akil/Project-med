package org.example.backend_med.Services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class MedicalAiService {
    @Autowired
    private   WebClient webClient;
    @Value("$openai.model")
    private String model;

    private  static final String SYSTEM_PROMPT = """
                    You are a medical assistant chatbot.
                    Rules:
                    - Do NOT diagnose diseases
                    - Do NOT prescribe medication
                    - Only suggest medical specialties
                    - Ask short follow-up questions
                    - Always include a disclaimer
                    - If symptoms seem serious, advise emergency care
            
            """;
    public String getReply(String userMessage){
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", userMessage)
                )
        );
        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    var choices = (List<Map<String, Object>>) response.get("choices");
                    var message = (Map<String, Object>) choices.get(0).get("message");
                    return message.get("content").toString();
                })
                .block();

    }

}
