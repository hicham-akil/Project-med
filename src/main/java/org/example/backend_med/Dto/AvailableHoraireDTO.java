package org.example.backend_med.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailableHoraireDTO {
    private Long idHoraire;
    private String joursSemaine;
    private String heureDebut;      // Available start time
    private String heureFin;        // Available end time
    private String status;
    private Long medecinId;
    private String medecinNom;
    private boolean partiallyBooked; // Indicates if original horaire was split
}