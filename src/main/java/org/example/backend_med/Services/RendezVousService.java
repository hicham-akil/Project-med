package org.example.backend_med.Services;

import lombok.RequiredArgsConstructor;
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
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
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

    /**
     * Create a new rendez-vous with notifications for patient and medecin
     */
    public RendezVous createRendezVous(RendezVous rendezVous) {

        // 1️⃣ Fetch full patient and medecin from DB
        Patient patient = patientRepo.findById(rendezVous.getPatient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        Medecin medecin = medecinRepo.findById(rendezVous.getMedecin().getId())
                .orElseThrow(() -> new IllegalArgumentException("Médecin not found"));

        rendezVous.setPatient(patient);
        rendezVous.setMedecin(medecin);

        // 1.5️⃣ Fetch and validate horaire if provided
        if (rendezVous.getHoraire() != null && rendezVous.getHoraire().getIdHoraire() != null) {
            Horaire horaire = horaireRepo.findById(rendezVous.getHoraire().getIdHoraire())
                    .orElseThrow(() -> new IllegalArgumentException("Horaire not found"));

            // Verify horaire belongs to the medecin
            if (!horaire.getMedecin().getId().equals(medecin.getId())) {
                throw new IllegalArgumentException("Cet horaire n'appartient pas au médecin sélectionné");
            }

            // Verify horaire is ACTIVE
            if (!"ACTIVE".equals(horaire.getStatus())) {
                throw new IllegalArgumentException("Cet horaire n'est pas actif");
            }

            rendezVous.setHoraire(horaire);
        }

        // 2️⃣ Validate dateHeureDebut and dateHeureFin are present
        if (rendezVous.getDateHeureDebut() == null) {
            throw new IllegalArgumentException("La date et heure de début sont obligatoires");
        }

        if (rendezVous.getDateHeureFin() == null) {
            throw new IllegalArgumentException("La date et heure de fin sont obligatoires");
        }

        // Validate end time is after start time
        if (rendezVous.getDateHeureFin().isBefore(rendezVous.getDateHeureDebut())
                || rendezVous.getDateHeureFin().isEqual(rendezVous.getDateHeureDebut())) {
            throw new IllegalArgumentException("L'heure de fin doit être après l'heure de début");
        }

        // 3️⃣ Validate against horaires disponibles
        if (!isWithinHorairesDisponibles(medecin, rendezVous.getDateHeureDebut(), rendezVous.getDateHeureFin())) {
            throw new IllegalArgumentException(
                    "Le médecin n'est pas disponible à ce créneau horaire. " +
                            "Veuillez vérifier les horaires disponibles du médecin."
            );
        }

        // 4️⃣ Validate time slot availability (no conflicts with other appointments)
        if (!isTimeSlotAvailable(medecin.getId(), rendezVous.getDateHeureDebut(), rendezVous.getDateHeureFin())) {
            throw new IllegalArgumentException("Ce créneau horaire est déjà réservé");
        }

        // 5️⃣ Validate date is not in the past
        if (rendezVous.getDateHeureDebut().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La date du rendez-vous ne peut pas être dans le passé");
        }

        // 6️⃣ Set default status if not provided
        if (rendezVous.getStatus() == null || rendezVous.getStatus().isEmpty()) {
            rendezVous.setStatus("EN_ATTENTE");
        }

        // 7️⃣ Save the rendez-vous in DB
        RendezVous savedRdv = rendezVousRepo.save(rendezVous);

        // 8️⃣ Notifications
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm");

        String patientMessage = "Votre rendez-vous avec le Dr " + medecin.getNom() +
                " est confirmé pour le " + savedRdv.getDateHeureDebut().format(formatter) +
                " (durée: " + calculateDuration(savedRdv.getDateHeureDebut(), savedRdv.getDateHeureFin()) + " minutes)";

        String medecinMessage = "Vous avez un nouveau rendez-vous avec le patient " +
                patient.getNom() + " le " + savedRdv.getDateHeureDebut().format(formatter) +
                " (durée: " + calculateDuration(savedRdv.getDateHeureDebut(), savedRdv.getDateHeureFin()) + " minutes)";

        // Send notification only if email is present
        if (patient.getEmail() != null && !patient.getEmail().isEmpty()) {
            notificationService.notify(patient, patientMessage, NotificationType.PATIENT);
        }

        if (medecin.getEmail() != null && !medecin.getEmail().isEmpty()) {
            notificationService.notify(medecin, medecinMessage, NotificationType.MEDECIN);
        }

        // 9️⃣ Return saved rendez-vous
        return savedRdv;
    }

    /**
     * Helper method: Calculate duration in minutes
     */
    private long calculateDuration(LocalDateTime start, LocalDateTime end) {
        return Duration.between(start, end).toMinutes();
    }

    /**
     * Validate if the rendez-vous time matches medecin's horaires disponibles
     * Updated to support dateHeureDebut and dateHeureFin
     */
    private boolean isWithinHorairesDisponibles(Medecin medecin, LocalDateTime startTime, LocalDateTime endTime) {
        DayOfWeek dayOfWeek = startTime.getDayOfWeek();
        String dayName = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase();

        // Get horaire for this specific day
        List<Horaire> horaires = horaireRepo.findByMedecinIdAndJoursSemaine(medecin.getId(), dayName);

        if (horaires.isEmpty()) {
            return false; // No horaire defined for this day
        }

        Horaire horaire = horaires.get(0);

        // Check if horaire is ACTIVE
        if (!"ACTIVE".equals(horaire.getStatus())) {
            return false;
        }

        LocalTime appointmentStartTime = startTime.toLocalTime();
        LocalTime appointmentEndTime = endTime.toLocalTime();
        LocalTime horaireStart = LocalTime.parse(horaire.getHeureDebut());
        LocalTime horaireEnd = LocalTime.parse(horaire.getHeureFin());

        // Check if appointment time is within horaire time range
        return !appointmentStartTime.isBefore(horaireStart)
                && !appointmentEndTime.isAfter(horaireEnd);
    }

    /**
     * Check if time slot is available (no overlapping appointments)
     * Updated to support dateHeureDebut and dateHeureFin
     */
    private boolean isTimeSlotAvailable(Long medecinId, LocalDateTime startTime, LocalDateTime endTime) {
        // Check if there are any overlapping appointments
        long overlapping = rendezVousRepo.countOverlappingAppointments(
                medecinId,
                null,  // horaireId not needed for this check
                startTime,
                endTime
        );

        return overlapping == 0;
    }

    /**
     * OLD METHOD - kept for backward compatibility if needed
     * Validate if the rendez-vous time matches medecin's horaires disponibles
     */
    @Deprecated
    private boolean isWithinHorairesDisponibles(Medecin medecin, LocalDateTime dateTime) {
        // Get day of week in French
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        String jourSemaine = getDayInFrench(dayOfWeek);

        // Get time
        LocalTime time = dateTime.toLocalTime();

        // Check if medecin has horaires disponibles
        List<Horaire> horaires = medecin.getHorairesDisponibles();

        if (horaires == null || horaires.isEmpty()) {
            return false;
        }

        // Find matching horaire
        for (Horaire horaire : horaires) {
            // Check if day matches
            if (!jourSemaine.equalsIgnoreCase(horaire.getJoursSemaine())) {
                continue;
            }

            // Check if status is disponible
            if (!"disponible".equalsIgnoreCase(horaire.getStatus())) {
                continue;
            }

            // Parse times
            LocalTime heureDebut = LocalTime.parse(horaire.getHeureDebut());
            LocalTime heureFin = LocalTime.parse(horaire.getHeureFin());

            // Check if time is within range (inclusive start, exclusive end)
            if (!time.isBefore(heureDebut) && time.isBefore(heureFin)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Convert DayOfWeek to French day name
     */
    private String getDayInFrench(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY: return "Lundi";
            case TUESDAY: return "Mardi";
            case WEDNESDAY: return "Mercredi";
            case THURSDAY: return "Jeudi";
            case FRIDAY: return "Vendredi";
            case SATURDAY: return "Samedi";
            case SUNDAY: return "Dimanche";
            default: return "";
        }
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

        // Check if rescheduling and validate availability
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

        if (rendezVous.getMedecin() != null) {
            existing.setMedecin(rendezVous.getMedecin());
        }

        if (rendezVous.getPatient() != null) {
            existing.setPatient(rendezVous.getPatient());
        }

        if (rendezVous.getHoraire() != null) {
            existing.setHoraire(rendezVous.getHoraire());
        }

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

        // Validate new dates are not in the past
        if (newStartDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La nouvelle date ne peut pas être dans le passé");
        }

        // Validate end is after start
        if (newEndDateTime.isBefore(newStartDateTime) || newEndDateTime.isEqual(newStartDateTime)) {
            throw new IllegalArgumentException("L'heure de fin doit être après l'heure de début");
        }

        Medecin medecin = rendezVous.getMedecin();

        // Check if within horaires disponibles
        if (!isWithinHorairesDisponibles(medecin, newStartDateTime, newEndDateTime)) {
            throw new IllegalArgumentException("Le médecin n'est pas disponible à ce créneau horaire");
        }

        // Check if new time slot is available
        if (!isTimeSlotAvailable(medecin.getId(), newStartDateTime, newEndDateTime)) {
            throw new IllegalArgumentException("Ce créneau horaire est déjà réservé");
        }

        rendezVous.setDateHeureDebut(newStartDateTime);
        rendezVous.setDateHeureFin(newEndDateTime);
        rendezVous.setStatus("REPORTE");

        return rendezVousRepo.save(rendezVous);
    }

    /**
     * OLD METHOD - kept for backward compatibility
     */
    @Deprecated
    public RendezVous rescheduleRendezVous(Long id, LocalDateTime newDateTime) {
        // Assume 30 minutes duration by default
        return rescheduleRendezVous(id, newDateTime, newDateTime.plusMinutes(30));
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
        // For backward compatibility - assume 30 minute slot
        return isTimeSlotAvailable(medecinId, dateTime, dateTime.plusMinutes(30));
    }

    @Override
    @Transactional(readOnly = true)
    public long countRendezVousByMedecinAndDate(Long medecinId, LocalDate date) {
        return rendezVousRepo.countByMedecinAndDate(medecinId, date);
    }
}