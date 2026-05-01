package org.example.backend_med.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QueuePositionDto {
    private Long   rendezVousId;
    private Long   patientId;
    private Long   medecinId;
    private int    queueNumber;
    private String status;
    private long   position;      // people BEFORE you (0 = you're next or in progress)
    private int    waitMinutes;
    private boolean calledNow;    // true when status = EN_COURS
}