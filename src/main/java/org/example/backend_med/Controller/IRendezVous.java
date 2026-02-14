package org.example.backend_med.Controller;

import org.example.backend_med.Models.RendezVous;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IRendezVous {
    RendezVous createRendezVous(RendezVous rendezVous);
    Optional<RendezVous> getRendezVousById(Long id);
    List<RendezVous> getAllRendezVous();
    List<RendezVous> getRendezVousByPatientId(Long patientId);
    List<RendezVous> getRendezVousByMedecinId(Long medecinId);
    List<RendezVous> getRendezVousByDate(LocalDate date);
    List<RendezVous> getRendezVousByDateRange(LocalDate startDate, LocalDate endDate);
    List<RendezVous> getRendezVousByStatus(String status);
    List<RendezVous> getRendezVousByMedecinAndDate(Long medecinId, LocalDate date);
    List<RendezVous> getRendezVousByPatientAndStatus(Long patientId, String status);
    List<RendezVous> getUpcomingRendezVousByPatientId(Long patientId);
    List<RendezVous> getUpcomingRendezVousByMedecinId(Long medecinId);
    List<RendezVous> getPastRendezVousByPatientId(Long patientId);
    RendezVous updateRendezVous(Long id, RendezVous rendezVous);
    RendezVous updateRendezVousStatus(Long id, String status);

    // Old method for backward compatibility
    RendezVous rescheduleRendezVous(Long id, LocalDateTime newDateTime);

    // New method with start and end times
    RendezVous rescheduleRendezVous(Long id, LocalDateTime newStartDateTime, LocalDateTime newEndDateTime);

    RendezVous confirmRendezVous(Long id);
    RendezVous cancelRendezVous(Long id);
    void deleteRendezVous(Long id);
    boolean existsById(Long id);
    boolean isTimeSlotAvailable(Long medecinId, LocalDateTime dateTime);
    long countRendezVousByMedecinAndDate(Long medecinId, LocalDate date);
}