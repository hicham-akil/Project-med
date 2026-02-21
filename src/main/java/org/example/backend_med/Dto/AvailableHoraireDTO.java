package org.example.backend_med.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailableHoraireDTO {
    private Long idHoraire;
    private LocalDate date;
    private String heureDebut;
    private String heureFin;
    private String status;
    private Long medecinId;
    private String medecinNom;
    private boolean partiallyBooked;

}