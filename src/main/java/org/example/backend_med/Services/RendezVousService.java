package org.example.backend_med.Services;

import lombok.RequiredArgsConstructor;
import org.example.backend_med.Dto.CreateRendezVousRequest;
import org.example.backend_med.Dto.RendezVousResponseDto;
import org.example.backend_med.Models.*;
import org.example.backend_med.Repository.*;
import org.example.backend_med.websocket.QueueWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RendezVousService implements IRendezVous {

    @Autowired private QueueWebSocketHandler wsHandler;
    @Autowired private RendezVousRepo rendezVousRepo;
    @Autowired private NotificationService notificationService;
    @Autowired private PatientRepo patientRepo;
    @Autowired private MedecinRepo medecinRepo;
    @Autowired private HoraireRepo horaireRepo;
    @Autowired private SpecialiteRepo specialiteRepo;

    // ─────────────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────────────
    @Override
    public RendezVous createRendezVous(CreateRendezVousRequest req) {
        Patient patient = patientRepo.findById(req.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient introuvable"));

        boolean hasActive = rendezVousRepo.existsActiveRendezVous(
                req.getPatientId(), req.getMedecinId()
        );
        if (hasActive) {
            throw new IllegalStateException("Vous avez déjà un rendez-vous actif avec ce médecin");
        }

        Medecin medecin = medecinRepo.findById(req.getMedecinId())
                .orElseThrow(() -> new IllegalArgumentException("Médecin introuvable"));

        Horaire horaire = horaireRepo.findById(req.getHoraireId())
                .orElseThrow(() -> new IllegalArgumentException("Horaire introuvable"));

        Specialite specialite = specialiteRepo.findById(req.getSpecialiteId())
                .orElseThrow(() -> new IllegalArgumentException("Spécialité introuvable"));

        long existingCount = rendezVousRepo.countByMedecinAndDate(medecin.getId(), req.getDate());
        int queueNumber = (int) existingCount + 1;

        RendezVous rdv = new RendezVous();
        rdv.setPatient(patient);
        rdv.setMedecin(medecin);
        rdv.setHoraire(horaire);
        rdv.setSpecialite(specialite);
        rdv.setQueueNumber(queueNumber);
        rdv.setStatus("EN_ATTENTE");

        RendezVous saved = rendezVousRepo.save(rdv);

        // ✅ WebSocket: push initial position to patient
        // The handler queries the DB itself — no position math here
        wsHandler.notifyPatient(patient.getId(), medecin.getId());

        try {
            notificationService.notify(patient,
                    "Votre rendez-vous est confirmé avec le médecin " + medecin.getNom(),
                    NotificationType.PATIENT);
            notificationService.notify(medecin,
                    "Vous avez un nouveau rendez-vous avec le patient " + patient.getNom(),
                    NotificationType.MEDECIN);
        } catch (Exception e) {
            System.err.println("NOTIFICATION ERROR");
            e.printStackTrace();
        }

        return saved;
    }

    // ─────────────────────────────────────────────
    // CALL NEXT — doctor clicks "Next Patient"
    // ─────────────────────────────────────────────
    @Override
    public RendezVousResponseDto callNextPatient(Long medecinId) {
        // 1. Mark current EN_COURS -> COMPLETED
        Optional<RendezVous> inProgress = rendezVousRepo.findInProgressByMedecin(medecinId);
        inProgress.ifPresent(rdv -> {
            rdv.setStatus("COMPLETED");
            rendezVousRepo.save(rdv);
        });

        // 2. Get the waiting list
        List<RendezVous> waitingList = rendezVousRepo.findWaitingByMedecinAndDate(
                medecinId, LocalDate.now()
        );
        if (waitingList.isEmpty()) {
            throw new IllegalStateException("Aucun patient en attente");
        }

        // 3. Call the next patient
        RendezVous next = waitingList.get(0);
        next.setStatus("EN_COURS");
        RendezVous saved = rendezVousRepo.save(next);

        // ✅ WebSocket: tell called patient it's their turn
        wsHandler.notifyCalledPatient(saved.getPatient().getId());

        // ✅ WebSocket: recalculate positions for all remaining EN_ATTENTE patients
        wsHandler.notifyWaitingPatients(medecinId);

        return toDto(saved);
    }

    // ─────────────────────────────────────────────
    // GET QUEUE — today's queue for a doctor
    // ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<RendezVousResponseDto> getTodayQueueByMedecin(Long medecinId) {
        return rendezVousRepo.findWaitingByMedecinAndDate(medecinId, LocalDate.now())
                .stream().map(this::toDto).toList();
    }

    // ─────────────────────────────────────────────
    // STATUS UPDATE — manual override
    // ─────────────────────────────────────────────
    @Override
    public RendezVousResponseDto updateStatus(Long id, String status) {
        RendezVous rdv = rendezVousRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous introuvable : " + id));

        List<String> validStatuses = List.of("EN_ATTENTE", "EN_COURS", "COMPLETED", "ANNULE");
        if (!validStatuses.contains(status.toUpperCase())) {
            throw new IllegalArgumentException("Statut invalide : " + status);
        }

        rdv.setStatus(status.toUpperCase());
        RendezVous saved = rendezVousRepo.save(rdv);

        switch (saved.getStatus()) {
            case "EN_COURS" ->
                // ✅ WebSocket: patient is being called right now
                    wsHandler.notifyCalledPatient(saved.getPatient().getId());

            case "ANNULE", "COMPLETED" ->
                // ✅ WebSocket: queue shifted, recalculate everyone ahead
                    wsHandler.notifyWaitingPatients(saved.getMedecin().getId());
        }

        return toDto(saved);
    }

    // ─────────────────────────────────────────────
    // CANCEL
    // ─────────────────────────────────────────────
    @Override
    public RendezVous cancelRendezVous(Long id) {
        RendezVous rdv = rendezVousRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous introuvable : " + id));

        rdv.setStatus("ANNULE");
        RendezVous cancelled = rendezVousRepo.save(rdv);

        // ✅ WebSocket: queue shifted, push updated positions to remaining patients
        wsHandler.notifyWaitingPatients(cancelled.getMedecin().getId());

        return cancelled;
    }

    // ─────────────────────────────────────────────
    // STANDARD CRUD
    // ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public Optional<RendezVous> getRendezVousById(Long id) {
        return rendezVousRepo.findById(id);
    }

    @Override
    public List<RendezVous> getAllRendezVous() {
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVousResponseDto> getRendezVousByPatientId(Long patientId) {
        return rendezVousRepo.findByPatientId(patientId)
                .stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVousResponseDto> getRendezVousByMedecinId(Long medecinId) {
        return rendezVousRepo.findAllByMedecinId(medecinId)
                .stream().map(this::toDto).toList();
    }

    @Override
    public List<RendezVous> getRendezVousByDate(LocalDate date) {
        return List.of();
    }

    @Override
    public void deleteRendezVous(Long id) {
        if (!rendezVousRepo.existsById(id)) {
            throw new IllegalArgumentException("Rendez-vous introuvable : " + id);
        }
        rendezVousRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return rendezVousRepo.existsById(id);
    }

    // ─────────────────────────────────────────────
    // HELPER — entity to DTO
    // ─────────────────────────────────────────────
    private RendezVousResponseDto toDto(RendezVous rdv) {
        return new RendezVousResponseDto(
                rdv.getId(),
                rdv.getStatus(),
                rdv.getMedecin() != null ? rdv.getMedecin().getId() : null,
                rdv.getPatient() != null ? rdv.getPatient().getNom() : null,
                rdv.getMedecin() != null ? rdv.getMedecin().getNom() : null,
                rdv.getSpecialite() != null ? rdv.getSpecialite().getNomspecialite() : null,
                rdv.getQueueNumber(),
                rdv.getHoraire().getDate()
        );
    }
}