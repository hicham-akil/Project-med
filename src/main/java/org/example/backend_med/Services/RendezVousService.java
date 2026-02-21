package org.example.backend_med.Services;

import lombok.RequiredArgsConstructor;
import org.example.backend_med.Dto.CreateRendezVousRequest;
import org.example.backend_med.Models.*;
import org.example.backend_med.Repository.HoraireRepo;
import org.example.backend_med.Repository.MedecinRepo;
import org.example.backend_med.Repository.PatientRepo;
import org.example.backend_med.Repository.RendezVousRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RendezVousService implements IRendezVous {

    @Autowired
    private RendezVousRepo rendezVousRepo;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private PatientRepo patientRepo;
    @Autowired
    private MedecinRepo medecinRepo;
    @Autowired
    private HoraireRepo horaireRepo;

    @Override
    public RendezVous createRendezVous(CreateRendezVousRequest req) {
        // 1️⃣ Fetch entities
        Patient patient = patientRepo.findById(req.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        Medecin medecin = medecinRepo.findById(req.getMedecinId())
                .orElseThrow(() -> new IllegalArgumentException("Médecin not found"));

        Horaire horaire = horaireRepo.findById(req.getHoraireId())
                .orElseThrow(() -> new IllegalArgumentException("Horaire not found"));

        // 2️⃣ Parse times
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime startTime = LocalTime.parse(req.getHeureDebut(), fmt);
        LocalTime endTime   = LocalTime.parse(req.getHeureFin(), fmt);

        // ✅ Date comes from horaire — no mismatch possible
        LocalDateTime start = LocalDateTime.of(horaire.getDate(), startTime);
        LocalDateTime end   = LocalDateTime.of(horaire.getDate(), endTime);

        // 3️⃣ Duration rule (1h–2h)
        long duration = Duration.between(start, end).toMinutes();
        if (duration < 60 || duration > 120) {
            throw new IllegalArgumentException("Durée invalide (1h–2h)");
        }

        // 4️⃣ Check within horaire time range
        if (!isWithinHoraire(horaire, start, end)) {
            throw new IllegalArgumentException("Médecin indisponible");
        }

        // 5️⃣ Check no overlapping appointments
        if (!isTimeSlotAvailable(medecin.getId(), start, end)) {
            throw new IllegalArgumentException("Créneau déjà réservé");
        }

        // 6️⃣ Build and save
        RendezVous rdv = new RendezVous();
        rdv.setPatient(patient);
        rdv.setMedecin(medecin);
        rdv.setHoraire(horaire);
        rdv.setDateHeureDebut(start);
        rdv.setDateHeureFin(end);
        rdv.setStatus("EN_ATTENTE");

        return rendezVousRepo.save(rdv);
    }

    // ✅ Simple check: is the appointment within the horaire's time range and date?
    private boolean isWithinHoraire(Horaire horaire, LocalDateTime start, LocalDateTime end) {
        if (!"ACTIVE".equals(horaire.getStatus())) return false;

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime hStart = LocalTime.parse(horaire.getHeureDebut(), fmt);
        LocalTime hEnd   = LocalTime.parse(horaire.getHeureFin(), fmt);

        // Date must match
        if (!start.toLocalDate().equals(horaire.getDate())) return false;

        // Time must be within bounds
        return !start.toLocalTime().isBefore(hStart) && !end.toLocalTime().isAfter(hEnd);
    }

    // ✅ Used in updateRendezVous and rescheduleRendezVous
    private boolean isWithinHorairesDisponibles(Medecin medecin, LocalDateTime start, LocalDateTime end) {
        List<Horaire> horaires = horaireRepo.findByMedecinIdAndDate(
                medecin.getId(), start.toLocalDate()
        );

        if (horaires.isEmpty()) return false;

        Horaire horaire = horaires.get(0);
        return isWithinHoraire(horaire, start, end);
    }

    private boolean isTimeSlotAvailable(Long medecinId, LocalDateTime startTime, LocalDateTime endTime) {
        long overlapping = rendezVousRepo.countOverlappingAppointments(
                medecinId,
                null,
                startTime,
                endTime
        );
        return overlapping == 0;
    }

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
    public List<RendezVous> getRendezVousByPatientId(Long patientId) {
        return rendezVousRepo.findByPatientId(patientId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> getRendezVousByMedecinId(Long medecinId) {
        return rendezVousRepo.findAllByMedecinId(medecinId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> getRendezVousByDate(LocalDate date) {
        return rendezVousRepo.findByDate(date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> getRendezVousByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return rendezVousRepo.findByDateRange(startDateTime, endDateTime);
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
    public RendezVous updateRendezVous(Long id, RendezVous rendezVous) {
        RendezVous existing = rendezVousRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous non trouvé avec l'ID: " + id));

        // Only re-validate if time changed
        if (!existing.getDateHeureDebut().equals(rendezVous.getDateHeureDebut()) ||
                !existing.getDateHeureFin().equals(rendezVous.getDateHeureFin())) {

            Medecin medecin = medecinRepo.findById(rendezVous.getMedecin().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Médecin not found"));

            if (!isWithinHorairesDisponibles(medecin, rendezVous.getDateHeureDebut(), rendezVous.getDateHeureFin())) {
                throw new IllegalArgumentException("Le médecin n'est pas disponible à ce créneau horaire");
            }

            if (!isTimeSlotAvailable(rendezVous.getMedecin().getId(), rendezVous.getDateHeureDebut(), rendezVous.getDateHeureFin())) {
                throw new IllegalArgumentException("Ce créneau horaire est déjà réservé");
            }
        }

        existing.setDateHeureDebut(rendezVous.getDateHeureDebut());
        existing.setDateHeureFin(rendezVous.getDateHeureFin());
        existing.setStatus(rendezVous.getStatus());
        existing.setTypeConsultation(rendezVous.getTypeConsultation());

        if (rendezVous.getMedecin() != null) existing.setMedecin(rendezVous.getMedecin());
        if (rendezVous.getPatient() != null) existing.setPatient(rendezVous.getPatient());
        if (rendezVous.getHoraire() != null) existing.setHoraire(rendezVous.getHoraire());

        return rendezVousRepo.save(existing);
    }

    @Override
    public RendezVous updateRendezVousStatus(Long id, String status) {
        RendezVous rendezVous = rendezVousRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous non trouvé avec l'ID: " + id));
        rendezVous.setStatus(status);
        return rendezVousRepo.save(rendezVous);
    }

    @Override
    public RendezVous rescheduleRendezVous(Long id, LocalDateTime newStartDateTime, LocalDateTime newEndDateTime) {
        RendezVous rendezVous = rendezVousRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous non trouvé avec l'ID: " + id));

        if (newStartDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La nouvelle date ne peut pas être dans le passé");
        }

        if (!newEndDateTime.isAfter(newStartDateTime)) {
            throw new IllegalArgumentException("L'heure de fin doit être après l'heure de début");
        }

        Medecin medecin = rendezVous.getMedecin();

        if (!isWithinHorairesDisponibles(medecin, newStartDateTime, newEndDateTime)) {
            throw new IllegalArgumentException("Le médecin n'est pas disponible à ce créneau horaire");
        }

        if (!isTimeSlotAvailable(medecin.getId(), newStartDateTime, newEndDateTime)) {
            throw new IllegalArgumentException("Ce créneau horaire est déjà réservé");
        }

        rendezVous.setDateHeureDebut(newStartDateTime);
        rendezVous.setDateHeureFin(newEndDateTime);
        rendezVous.setStatus("REPORTE");

        return rendezVousRepo.save(rendezVous);
    }

    @Override
    public RendezVous confirmRendezVous(Long id) {
        RendezVous rendezVous = rendezVousRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous non trouvé avec l'ID: " + id));
        rendezVous.setStatus("CONFIRME");
        return rendezVousRepo.save(rendezVous);
    }

    @Override
    public RendezVous cancelRendezVous(Long id) {
        RendezVous rendezVous = rendezVousRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous non trouvé avec l'ID: " + id));
        rendezVous.setStatus("ANNULE");
        return rendezVousRepo.save(rendezVous);
    }

    @Override
    public void deleteRendezVous(Long id) {
        if (!rendezVousRepo.existsById(id)) {
            throw new IllegalArgumentException("Rendez-vous non trouvé avec l'ID: " + id);
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
    public boolean isTimeSlotAvailable(Long medecinId, LocalDateTime dateTime) {
        return isTimeSlotAvailable(medecinId, dateTime, dateTime.plusMinutes(30));
    }

    @Override
    @Transactional(readOnly = true)
    public long countRendezVousByMedecinAndDate(Long medecinId, LocalDate date) {
        return rendezVousRepo.countByMedecinAndDate(medecinId, date);
    }
}