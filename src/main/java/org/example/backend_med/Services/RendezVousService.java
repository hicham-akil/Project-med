package org.example.backend_med.Services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend_med.Dto.CreateRendezVousRequest;
import org.example.backend_med.Dto.RendezVousResponseDto;
import org.example.backend_med.Models.*;
import org.example.backend_med.Repository.*;
import org.example.backend_med.websocket.QueueWebSocketHandler;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class RendezVousService implements IRendezVous {

    // ── All fields are final → Lombok @RequiredArgsConstructor generates the constructor.
    // ── No @Autowired anywhere — one injection style, no ambiguity.
    private final QueueWebSocketHandler      wsHandler;
    private final RendezVousRepo             rendezVousRepo;
    private final NotificationService        notificationService;
    private final PatientRepo                patientRepo;
    private final MedecinRepo                medecinRepo;
    private final HoraireRepo                horaireRepo;
    private final SpecialiteRepo             specialiteRepo;
    private final ApplicationEventPublisher  eventPublisher;   // for AFTER_COMMIT events

    // ─────────────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────────────
    @Override
    @Transactional
    public RendezVous createRendezVous(CreateRendezVousRequest req) {

        Patient patient = patientRepo.findById(req.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient introuvable : " + req.getPatientId()));

        boolean hasActive = rendezVousRepo.existsActiveRendezVous(
                req.getPatientId(), req.getMedecinId()
        );
        if (hasActive) {
            throw new IllegalStateException("Vous avez déjà un rendez-vous actif avec ce médecin");
        }

        Medecin medecin = medecinRepo.findById(req.getMedecinId())
                .orElseThrow(() -> new IllegalArgumentException("Médecin introuvable : " + req.getMedecinId()));

        Horaire horaire = horaireRepo.findById(req.getHoraireId())
                .orElseThrow(() -> new IllegalArgumentException("Horaire introuvable : " + req.getHoraireId()));

        Specialite specialite = specialiteRepo.findById(req.getSpecialiteId())
                .orElseThrow(() -> new IllegalArgumentException("Spécialité introuvable : " + req.getSpecialiteId()));

        // ✅ FIX 🔴 — PESSIMISTIC_WRITE locks the rows for this medecin+date.
        //    Any concurrent transaction calling the same method will WAIT here
        //    until this transaction commits, guaranteeing a unique queueNumber.
        //    The UNIQUE constraint on (medecin_id, date, queue_number) is a second
        //    safety net — add it in your migration if not already present.
        long existingCount = rendezVousRepo.countByMedecinAndDateWithLock(
                medecin.getId(), req.getDate()
        );
        int queueNumber = (int) existingCount + 1;

        RendezVous rdv = new RendezVous();
        rdv.setPatient(patient);
        rdv.setMedecin(medecin);
        rdv.setHoraire(horaire);
        rdv.setSpecialite(specialite);
        rdv.setQueueNumber(queueNumber);
        rdv.setStatus("EN_ATTENTE");

        RendezVous saved = rendezVousRepo.save(rdv);

        // ✅ FIX 🟡 — WebSocket and notification fired AFTER commit, not inside the TX.
        //    If save() succeeds but the TX rolls back later (e.g. constraint violation),
        //    the patient will NOT be notified of a non-existent appointment.
        eventPublisher.publishEvent(new RendezVousCreatedEvent(
                saved.getId(),
                patient.getId(),
                medecin.getId(),
                patient.getNom(),
                medecin.getNom()
        ));

        return saved;
    }

    /**
     * Fired only after the creating transaction commits successfully.
     * WebSocket push and notifications are side-effects — they must never
     * run for a rolled-back appointment.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRendezVousCreated(RendezVousCreatedEvent event) {
        wsHandler.notifyPatient(event.patientId(), event.medecinId());
        try {
            Patient patient = patientRepo.getReferenceById(event.patientId());
            Medecin medecin = medecinRepo.getReferenceById(event.medecinId());
            notificationService.notify(patient,
                    "Votre rendez-vous est confirmé avec le médecin " + event.medecinNom(),
                    NotificationType.PATIENT);
            notificationService.notify(medecin,
                    "Vous avez un nouveau rendez-vous avec le patient " + event.patientNom(),
                    NotificationType.MEDECIN);
        } catch (Exception e) {
            log.error("Échec envoi notification — rdvId={} patientId={} medecinId={}",
                    event.rdvId(), event.patientId(), event.medecinId(), e);
        }
    }

    // ─────────────────────────────────────────────
    // CALL NEXT — doctor clicks "Next Patient"
    // ─────────────────────────────────────────────
    @Override
    @Transactional
    public RendezVousResponseDto callNextPatient(Long medecinId) {

        // 1. Mark current EN_COURS → COMPLETED
        rendezVousRepo.findInProgressByMedecin(medecinId).ifPresent(rdv -> {
            rdv.setStatus("COMPLETED");
            rendezVousRepo.save(rdv);
        });

        // 2. Get waiting list for today
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

        // WebSocket after commit — patient notified only if TX succeeded
        long patientId = saved.getPatient().getId();
        eventPublisher.publishEvent(new PatientCalledEvent(patientId, medecinId));

        return toDto(saved);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPatientCalled(PatientCalledEvent event) {
        wsHandler.notifyCalledPatient(event.patientId());
        wsHandler.notifyWaitingPatients(event.medecinId());
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
    @Transactional
    public RendezVousResponseDto updateStatus(Long id, String status) {
        RendezVous rdv = rendezVousRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous introuvable : " + id));

        List<String> validStatuses = List.of("EN_ATTENTE", "EN_COURS", "COMPLETED", "ANNULE");
        String normalized = status.toUpperCase();
        if (!validStatuses.contains(normalized)) {
            throw new IllegalArgumentException("Statut invalide : " + status);
        }

        rdv.setStatus(normalized);
        RendezVous saved = rendezVousRepo.save(rdv);

        long patientId = saved.getPatient().getId();
        long medecinId = saved.getMedecin().getId();

        eventPublisher.publishEvent(new StatusUpdatedEvent(normalized, patientId, medecinId));

        return toDto(saved);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onStatusUpdated(StatusUpdatedEvent event) {
        switch (event.status()) {
            case "EN_COURS"  -> wsHandler.notifyCalledPatient(event.patientId());
            case "ANNULE",
                 "COMPLETED" -> wsHandler.notifyWaitingPatients(event.medecinId());
        }
    }

    // ─────────────────────────────────────────────
    // CANCEL
    // ─────────────────────────────────────────────
    @Override
    @Transactional
    public RendezVous cancelRendezVous(Long id) {
        RendezVous rdv = rendezVousRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous introuvable : " + id));

        rdv.setStatus("ANNULE");
        RendezVous cancelled = rendezVousRepo.save(rdv);

        long medecinId = cancelled.getMedecin().getId();
        eventPublisher.publishEvent(new StatusUpdatedEvent("ANNULE", cancelled.getPatient().getId(), medecinId));

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
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return rendezVousRepo.existsById(id);
    }

    // ✅ FIX 🟡 — These were silently returning empty lists.
    //    Throwing UnsupportedOperationException makes the contract explicit:
    //    "this method is not implemented" is better than "it returns nothing".
    //    Implement properly or remove from the interface if not needed.
    @Override
    public List<RendezVous> getAllRendezVous() {
        throw new UnsupportedOperationException(
                "getAllRendezVous() non implémenté — utiliser getRendezVousByMedecinId() ou getRendezVousByPatientId()"
        );
    }

    @Override
    public List<RendezVous> getRendezVousByDate(LocalDate date) {
        throw new UnsupportedOperationException(
                "getRendezVousByDate() non implémenté — filtrer via findWaitingByMedecinAndDate()"
        );
    }

    @Override
    public void deleteRendezVous(Long id) {
        if (!rendezVousRepo.existsById(id)) {
            throw new IllegalArgumentException("Rendez-vous introuvable : " + id);
        }
        rendezVousRepo.deleteById(id);
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
                rdv.getHoraire() != null ? rdv.getHoraire().getDate() : null
        );
    }

    // ─────────────────────────────────────────────
    // INTERNAL EVENTS — decouples TX from side-effects
    // ─────────────────────────────────────────────
    public record RendezVousCreatedEvent(long rdvId, long patientId, long medecinId,
                                         String patientNom, String medecinNom) {}

    public record PatientCalledEvent(long patientId, long medecinId) {}

    public record StatusUpdatedEvent(String status, long patientId, long medecinId) {}
}