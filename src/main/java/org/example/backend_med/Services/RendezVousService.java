package org.example.backend_med.Services;

import lombok.RequiredArgsConstructor;
import org.example.backend_med.Dto.CreateRendezVousRequest;
import org.example.backend_med.Dto.RendezVousResponseDto;
import org.example.backend_med.Models.*;
import org.example.backend_med.Repository.*;
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

    @Autowired private RendezVousRepo rendezVousRepo;
    @Autowired private NotificationService notificationService;
    @Autowired private PatientRepo patientRepo;
    @Autowired private MedecinRepo medecinRepo;
    @Autowired private HoraireRepo horaireRepo;
    @Autowired private SpecialiteRepo specialiteRepo;

    // ─────────────────────────────────────────────
    // CREATE — patient books, gets a queue number
    // ─────────────────────────────────────────────
    @Override
    public RendezVous createRendezVous(CreateRendezVousRequest req) {
        System.out.println(">>> patientId: " + req.getPatientId());
        System.out.println(">>> medecinId: " + req.getMedecinId());
        System.out.println(">>> horaireId: " + req.getHoraireId());
        System.out.println(">>> specialiteId: " + req.getSpecialiteId());
        Patient patient = patientRepo.findById(req.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient introuvable"));

        Medecin medecin = medecinRepo.findById(req.getMedecinId())
                .orElseThrow(() -> new IllegalArgumentException("Médecin introuvable"));

        Horaire horaire = horaireRepo.findById(req.getHoraireId())
                .orElseThrow(() -> new IllegalArgumentException("Horaire introuvable"));

        Specialite specialite = specialiteRepo.findById(req.getSpecialiteId())
                .orElseThrow(() -> new IllegalArgumentException("Spécialité introuvable"));

        LocalDate date = req.getDate();

        // Assign queue number: how many non-cancelled bookings exist for this doctor today + 1
        long existingCount = rendezVousRepo.countByMedecinAndDate(medecin.getId(), date);
        int queueNumber = (int) existingCount + 1;

        RendezVous rdv = new RendezVous();
        rdv.setPatient(patient);
        rdv.setMedecin(medecin);
        rdv.setHoraire(horaire);
        rdv.setSpecialite(specialite);
        rdv.setDateHeureDebut(date.atStartOfDay());
        rdv.setDateHeureFin(null);
        rdv.setQueueNumber(queueNumber);
        rdv.setStatus("EN_ATTENTE");

        return rendezVousRepo.save(rdv);
    }

    // ─────────────────────────────────────────────
    // CALL NEXT — doctor clicks "Next Patient"
    // ─────────────────────────────────────────────
    @Override
    public RendezVousResponseDto callNextPatient(Long medecinId) {
        // 1. Mark the current EN_COURS appointment as COMPLETED
        Optional<RendezVous> inProgress = rendezVousRepo.findInProgressByMedecin(medecinId);
        inProgress.ifPresent(rdv -> {
            rdv.setStatus("COMPLETED");
            rendezVousRepo.save(rdv);
        });

        // 2. Get the next waiting patient (lowest queue number today)
        List<RendezVous> waitingList = rendezVousRepo.findWaitingByMedecinAndDate(
                medecinId, LocalDate.now()
        );

        if (waitingList.isEmpty()) {
            throw new IllegalStateException("Aucun patient en attente");
        }

        RendezVous next = waitingList.get(0); // lowest queueNumber
        next.setStatus("EN_COURS");
        RendezVous saved = rendezVousRepo.save(next);

        return toDto(saved);
    }

    // ─────────────────────────────────────────────
    // GET QUEUE — today's queue for a doctor
    // ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<RendezVousResponseDto> getTodayQueueByMedecin(Long medecinId) {
        List<RendezVous> list = rendezVousRepo.findWaitingByMedecinAndDate(
                medecinId, LocalDate.now()
        );
        return list.stream().map(this::toDto).toList();
    }

    // ─────────────────────────────────────────────
    // STATUS UPDATE — manual override
    // ─────────────────────────────────────────────
    @Override
    public RendezVousResponseDto updateStatus(Long id, String status) {
        RendezVous rdv = rendezVousRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous introuvable : " + id));

        List<String> validStatuses = List.of(
                "EN_ATTENTE", "EN_COURS", "COMPLETED", "ANNULE"
        );
        if (!validStatuses.contains(status.toUpperCase())) {
            throw new IllegalArgumentException("Statut invalide : " + status);
        }

        rdv.setStatus(status.toUpperCase());
        return toDto(rendezVousRepo.save(rdv));
    }

    // ─────────────────────────────────────────────
    // STANDARD CRUD (unchanged logic)
    // ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public Optional<RendezVous> getRendezVousById(Long id) {
        return rendezVousRepo.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> getAllRendezVous() {
        return rendezVousRepo.findAllByOrderByDateHeureDebutDesc();
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
    public List<RendezVous> getRendezVousByDate(LocalDate date) {
        return rendezVousRepo.findByDate(date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> getRendezVousByDateRange(LocalDate startDate, LocalDate endDate) {
        return rendezVousRepo.findByDateRange(
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> getRendezVousByStatus(String status) {
        return rendezVousRepo.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> getRendezVousByMedecinAndDate(Long medecinId, LocalDate date) {
        return rendezVousRepo.findByMedecinAndDate(medecinId, date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> getRendezVousByPatientAndStatus(Long patientId, String status) {
        return rendezVousRepo.findByPatientAndStatus(patientId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> getUpcomingRendezVousByPatientId(Long patientId) {
        return rendezVousRepo.findUpcomingByPatientId(patientId, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> getUpcomingRendezVousByMedecinId(Long medecinId) {
        return rendezVousRepo.findUpcomingByMedecinId(medecinId, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> getPastRendezVousByPatientId(Long patientId) {
        return rendezVousRepo.findPastByPatientId(patientId, LocalDateTime.now());
    }

    @Override
    public RendezVous cancelRendezVous(Long id) {
        RendezVous rdv = rendezVousRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous introuvable : " + id));
        rdv.setStatus("ANNULE");
        return rendezVousRepo.save(rdv);
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

    @Override
    @Transactional(readOnly = true)
    public long countRendezVousByMedecinAndDate(Long medecinId, LocalDate date) {
        return rendezVousRepo.countByMedecinAndDate(medecinId, date);
    }

    // Removed: rescheduleRendezVous, updateRendezVous (time-based),
    //          isTimeSlotAvailable, isWithinHoraire, isWithinHorairesDisponibles

    // ─────────────────────────────────────────────
    // HELPER — entity to DTO
    // ─────────────────────────────────────────────
    private RendezVousResponseDto toDto(RendezVous rdv) {
        return new RendezVousResponseDto(
                rdv.getId(),
                rdv.getDateHeureDebut(),
                rdv.getDateHeureFin(),
                rdv.getStatus(),
                rdv.getMedecin() != null ? rdv.getMedecin().getId() : null,
                rdv.getPatient() != null ? rdv.getPatient().getNom() : null,
                rdv.getMedecin() != null ? rdv.getMedecin().getNom() : null,
                rdv.getSpecialite() != null ? rdv.getSpecialite().getNomspecialite() : null,
                rdv.getQueueNumber()
        );
    }
}