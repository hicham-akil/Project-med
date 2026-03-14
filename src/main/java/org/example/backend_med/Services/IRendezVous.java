package org.example.backend_med.Services;

import org.example.backend_med.Dto.CreateRendezVousRequest;
import org.example.backend_med.Dto.RendezVousResponseDto;
import org.example.backend_med.Models.RendezVous;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IRendezVous {

    // ── QUEUE (new) ──
    RendezVous createRendezVous(CreateRendezVousRequest req);
    RendezVousResponseDto callNextPatient(Long medecinId);
    List<RendezVousResponseDto> getTodayQueueByMedecin(Long medecinId);

    // ── STATUS ──
    RendezVousResponseDto updateStatus(Long id, String status);
    RendezVous cancelRendezVous(Long id);

    // ── GET ──
    Optional<RendezVous> getRendezVousById(Long id);
    List<RendezVous> getAllRendezVous();
    List<RendezVousResponseDto> getRendezVousByPatientId(Long patientId);
    List<RendezVousResponseDto> getRendezVousByMedecinId(Long medecinId);
    List<RendezVous> getRendezVousByDate(LocalDate date);
    List<RendezVous> getRendezVousByDateRange(LocalDate startDate, LocalDate endDate);
    List<RendezVous> getRendezVousByStatus(String status);
    List<RendezVous> getRendezVousByMedecinAndDate(Long medecinId, LocalDate date);
    List<RendezVous> getRendezVousByPatientAndStatus(Long patientId, String status);
    List<RendezVous> getUpcomingRendezVousByPatientId(Long patientId);
    List<RendezVous> getUpcomingRendezVousByMedecinId(Long medecinId);
    List<RendezVous> getPastRendezVousByPatientId(Long patientId);

    // ── UTILS ──
    void deleteRendezVous(Long id);
    boolean existsById(Long id);
    long countRendezVousByMedecinAndDate(Long medecinId, LocalDate date);

    // ── REMOVED (time-based, no longer needed) ──
    // rescheduleRendezVous
    // updateRendezVous
    // confirmRendezVous
    // isTimeSlotAvailable
}