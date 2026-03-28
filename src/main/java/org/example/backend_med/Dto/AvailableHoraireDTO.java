package org.example.backend_med.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailableHoraireDTO {
    private Long idHoraire;
    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String status;
    private Long medecinId;
    private String medecinNom;
    private boolean partiallyBooked;

}