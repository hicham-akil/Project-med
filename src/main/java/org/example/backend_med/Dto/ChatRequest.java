package org.example.backend_med.Dto;

public class ChatRequest {
    private String message;  // Le message envoyé par l'utilisateur

    // Constructeurs
    public ChatRequest() {}

    public ChatRequest(String message) {
        this.message = message;
    }

    // Getters et Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}