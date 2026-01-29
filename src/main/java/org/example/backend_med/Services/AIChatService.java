package org.example.backend_med.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Map;

@Service
public class AIChatService {

    private final WebClient webClient;

    @Value("${huggingface.api.key}")
    private String apiKey;

    @Value("${huggingface.api.url}")
    private String apiUrl;

    public AIChatService(WebClient webClient) {
        this.webClient = webClient;
    }

    public String askAI(String message) {
        Map<String, Object> payload = Map.of(
                "inputs", message,
                "parameters", Map.of("max_new_tokens", 100)
        );

        Mono<Map[]> responseMono = webClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Map[].class);

        Map[] response = responseMono.block(); // blocking call
        if (response != null && response.length > 0) {
            return response[0].get("generated_text").toString();
        } else {
            return "Sorry, I couldn't get a response.";
        }
    }
}
