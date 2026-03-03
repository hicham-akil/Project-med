package org.example.backend_med.Dto;

import java.time.LocalDateTime;

public record RendezVousResponseDto(
        Long id,
        LocalDateTime dateHeureDebut,
        LocalDateTime dateHeureFin,
        String status,
        Long medecinId,
        String medecinNom,
        String medecinSpecialite
) {}