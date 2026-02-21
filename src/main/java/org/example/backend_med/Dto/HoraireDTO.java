package org.example.backend_med.Dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class HoraireDTO {
    private Long medecinId;
    private LocalDate date;       // ✅ replaces joursSemaine + month + year
    private String heureDebut;
    private String heureFin;
    private String status;
}