package org.example.backend_med.Dto;

import java.time.LocalDateTime;

public record RendezVousResponseDto(
        Long id,

        String status,
        Long medecinId,
        String patientnom,
        String medecinNom,
        String specialite,
        Integer queueNumber
) {}