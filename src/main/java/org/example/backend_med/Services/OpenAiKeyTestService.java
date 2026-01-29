package org.example.backend_med.Services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OpenAiKeyTestService {
    @Value("${openai.api.key}")
    private String apiKey;
    @PostConstruct
    public void testkey(){
        System.out.println("Openai key loaded"+(apiKey!=null));
    }

}
