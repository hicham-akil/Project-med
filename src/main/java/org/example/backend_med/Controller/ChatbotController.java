package org.example.backend_med.Controller;

import org.example.backend_med.Dto.ChatRequest;
import org.example.backend_med.Dto.ChatResponse;
import org.example.backend_med.Services.ChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatbotController {

    private final ChatbotService chatbotService;

    // ✅ Constructor injection ONLY
    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping
    public Mono<ResponseEntity<ChatResponse>> chat(@RequestBody ChatRequest request) {
        return chatbotService.getResponse(request.getMessage())
                .map(response -> ResponseEntity.ok(new ChatResponse(response)));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Chatbot API fonctionne");
    }
}
