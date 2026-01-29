package org.example.backend_med.Controller;

import org.example.backend_med.Services.AIChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class AIChatController {
    @Autowired
    private AIChatService aiChatService;

    public AIChatController(AIChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping
    public String chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        return aiChatService.askAI(message);
    }
}
