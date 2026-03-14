package org.example.backend_med.Dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
@Data
public class CreateRendezVousRequest {

    private Long patientId;
    private Long medecinId;
    private Long horaireId;

    private LocalDate date;
    private Long specialiteId;

}