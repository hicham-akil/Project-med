package org.example.backend_med.Services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class ChatbotService {

    @Value("${huggingface.api.token}")
    private String apiToken;

    @Value("${huggingface.api.url}")
    private String apiUrl;

    @Value("${huggingface.api.model}")
    private String model;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public ChatbotService(WebClient.Builder builder, ObjectMapper objectMapper) {
        this.webClient = builder.build();
        this.objectMapper = objectMapper;
    }

    public Mono<String> getResponse(String userMessage) {

        if (userMessage == null || userMessage.isBlank()) {
            return Mono.just("Message utilisateur vide.");
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("inputs", userMessage);

        return webClient.post()
                .uri(apiUrl + "/" + model)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseResponse)
                .onErrorResume(this::handleError);
    }

    private String parseResponse(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);

            // HF Inference returns an ARRAY
            if (root.isArray() && root.size() > 0) {
                JsonNode first = root.get(0);
                if (first.has("generated_text")) {
                    return first.get("generated_text").asText();
                }
            }

            if (root.has("error")) {
                return "Erreur API: " + root.get("error").asText();
            }

            return "Réponse inattendue du modèle.";

        } catch (Exception e) {
            return "Erreur parsing réponse: " + e.getMessage();
        }
    }

    private Mono<String> handleError(Throwable error) {
        if (error instanceof WebClientResponseException webError) {

            int status = webError.getStatusCode().value();
            String body = webError.getResponseBodyAsString();

            System.err.println("❌ HF ERROR " + status);
            System.err.println(body);

            if (status == 503) {
                return Mono.just("⏳ Le modèle se charge. Réessayez dans quelques secondes.");
            }
            if (status == 401) {
                return Mono.just("🔑 Token Hugging Face invalide.");
            }

            return Mono.just("Erreur HF (" + status + ")");
        }

        return Mono.just("Erreur: " + error.getMessage());
    }
}
