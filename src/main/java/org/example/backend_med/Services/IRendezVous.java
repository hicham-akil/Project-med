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


    // ── UTILS ──
    void deleteRendezVous(Long id);

    boolean existsById(Long id);
}