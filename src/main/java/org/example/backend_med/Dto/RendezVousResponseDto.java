package org.example.backend_med.Dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public record RendezVousResponseDto(
        Long id,

        String status,
        Long medecinId,
        String patientnom,
        String medecinNom,
        String specialite,
        Integer queueNumber,
        LocalDate rendezvousdate

) {}