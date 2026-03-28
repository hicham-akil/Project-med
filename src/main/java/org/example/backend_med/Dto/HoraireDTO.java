package org.example.backend_med.Dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class HoraireDTO {
    private Long medecinId;
    private LocalDate date;       // ✅ replaces joursSemaine + month + year
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String status;
}