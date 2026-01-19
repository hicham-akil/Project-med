package org.example.backend_med.Services;

import org.example.backend_med.Models.RendezVous;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IRendezVous {

    // Create
    RendezVous createRendezVous(RendezVous rendezVous);

    // Read - Single
    Optional<RendezVous> getRendezVousById(Long id);

    // Read - All
    List<RendezVous> getAllRendezVous();

    // Read - By Patient
    List<RendezVous> getRendezVousByPatientId(Long patientId);

    // Read - By Medecin
    List<RendezVous> getRendezVousByMedecinId(Long medecinId);

    // Read - By Date
    List<RendezVous> getRendezVousByDate(LocalDate date);

    // Read - By Date Range
    List<RendezVous> getRendezVousByDateRange(LocalDate startDate, LocalDate endDate);

    // Read - By Status
    List<RendezVous> getRendezVousByStatus(String status);

    // Read - By Medecin and Date
    List<RendezVous> getRendezVousByMedecinAndDate(Long medecinId, LocalDate date);

    // Read - By Patient and Status
    List<RendezVous> getRendezVousByPatientAndStatus(Long patientId, String status);

    // Read - Upcoming Appointments
    List<RendezVous> getUpcomingRendezVousByPatientId(Long patientId);

    // Read - Upcoming Appointments by Medecin
    List<RendezVous> getUpcomingRendezVousByMedecinId(Long medecinId);

    // Read - Past Appointments
    List<RendezVous> getPastRendezVousByPatientId(Long patientId);

    // Update
    RendezVous updateRendezVous(Long id, RendezVous rendezVous);

    // Update - Status
    RendezVous updateRendezVousStatus(Long id, String status);

    // Update - Reschedule
    RendezVous rescheduleRendezVous(Long id, LocalDateTime newDateTime);

    // Update - Confirm Appointment
    RendezVous confirmRendezVous(Long id);

    // Update - Cancel Appointment
    RendezVous cancelRendezVous(Long id);

    // Delete
    void deleteRendezVous(Long id);

    // Check existence
    boolean existsById(Long id);

    // Check availability
    boolean isTimeSlotAvailable(Long medecinId, LocalDateTime dateTime);



    // Count - By Medecin and Date
    long countRendezVousByMedecinAndDate(Long medecinId, LocalDate date);
}