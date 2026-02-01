package org.example.backend_med.Dto;

public class ChatResponse {
    private String response;  // La réponse de l'IA

    public ChatResponse() {}

    public ChatResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}