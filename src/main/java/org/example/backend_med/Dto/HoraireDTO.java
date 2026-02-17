package org.example.backend_med.Dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HoraireDTO {
    private String joursSemaine;
    private String heureDebut;
    private String heureFin;
    private String month;
    private String year;
    private String status;
    private Long medecinId;

}
