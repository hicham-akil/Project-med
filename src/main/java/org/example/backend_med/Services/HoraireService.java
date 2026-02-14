package org.example.backend_med.Services;

import org.example.backend_med.Dto.AvailableHoraireDTO;
import org.example.backend_med.Models.Horaire;
import org.example.backend_med.Models.RendezVous;
import org.example.backend_med.Repository.HoraireRepo;
import org.example.backend_med.Repository.RendezVousRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class HoraireService implements IHoraire {

    @Autowired
    private HoraireRepo horaireRepo;

    @Autowired
    private RendezVousRepo rendezVousRepo;

    @Override
    public List<Horaire> createHoraire(List<Horaire> horaires) {
        List<Horaire> savedHoraires = new ArrayList<>();

        for (Horaire horaire : horaires) {
            Horaire toSave;

            // Check if this horaire already exists by idHoraire
            if (horaire.getIdHoraire() != null) {
                toSave = horaireRepo.findById(horaire.getIdHoraire())
                        .orElse(new Horaire());
            } else {
                // Check if horaire exists for this medecin and day
                List<Horaire> existing = horaireRepo.findByMedecinIdAndJoursSemaine(
                        horaire.getMedecin().getId(),
                        horaire.getJoursSemaine()
                );

                toSave = existing.isEmpty() ? new Horaire() : existing.get(0);
            }

            // Update all fields
            toSave.setJoursSemaine(horaire.getJoursSemaine());
            toSave.setMonth(horaire.getMonth());
            toSave.setYear(horaire.getYear());
            toSave.setHeureDebut(horaire.getHeureDebut());
            toSave.setHeureFin(horaire.getHeureFin());
            toSave.setStatus(horaire.getStatus());
            toSave.setMedecin(horaire.getMedecin());

            savedHoraires.add(horaireRepo.save(toSave));
        }

        return savedHoraires;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Horaire> getHoraireById(Long id) {
        return horaireRepo.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Horaire> getAllHoraires() {
        return horaireRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Horaire> getHorairesByMedecinId(Long medecinId) {
        return horaireRepo.findAllByMedecinId(medecinId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Horaire> getHorairesByJour(String joursSemaine) {
        return horaireRepo.findByJoursSemaine(joursSemaine);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Horaire> getHorairesByStatus(String status) {
        return horaireRepo.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Horaire> getHorairesByMedecinAndJour(Long medecinId, String joursSemaine) {
        return horaireRepo.findByMedecinIdAndJoursSemaine(medecinId, joursSemaine);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Horaire> getAvailableHorairesByMedecinId(Long medecinId) {
        // Get all ACTIVE horaires - they all have some availability
        return horaireRepo.findAvailableHorairesByMedecinId(medecinId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailableHoraireDTO> getAvailableHorairesWithSlots(Long medecinId) {
        // Get all ACTIVE horaires
        List<Horaire> activeHoraires = horaireRepo.findAvailableHorairesByMedecinId(medecinId);

        // Get all appointments for this medecin
        List<RendezVous> allAppointments = rendezVousRepo.findByMedecinId(medecinId);

        List<AvailableHoraireDTO> availableSlots = new ArrayList<>();

        for (Horaire horaire : activeHoraires) {
            // Get appointments for this specific day
            List<RendezVous> dayAppointments = allAppointments.stream()
                    .filter(rdv -> {
                        DayOfWeek rdvDay = rdv.getDateHeureDebut().getDayOfWeek();
                        String rdvDayName = rdvDay.getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase();
                        return rdvDayName.equals(horaire.getJoursSemaine());
                    })
                    .sorted(Comparator.comparing(RendezVous::getDateHeureDebut))
                    .collect(Collectors.toList());

            // Calculate available time ranges
            List<AvailableHoraireDTO> slots = calculateAvailableTimeRanges(horaire, dayAppointments);
            availableSlots.addAll(slots);
        }

        return availableSlots;
    }

    /**
     * Calculate available time ranges within a horaire, excluding booked appointments
     */
    private List<org.example.backend_med.Dto.AvailableHoraireDTO> calculateAvailableTimeRanges(Horaire horaire, List<RendezVous> appointments) {
        List<AvailableHoraireDTO> availableRanges = new ArrayList<>();

        LocalTime horaireStart = LocalTime.parse(horaire.getHeureDebut());
        LocalTime horaireEnd = LocalTime.parse(horaire.getHeureFin());

        if (appointments.isEmpty()) {
            // No appointments - entire horaire is available
            availableRanges.add(new AvailableHoraireDTO(
                    horaire.getIdHoraire(),
                    horaire.getJoursSemaine(),
                    horaire.getHeureDebut(),
                    horaire.getHeureFin(),
                    horaire.getStatus(),
                    horaire.getMedecin().getId(),
                    horaire.getMedecin().getNom(),
                    false
            ));
            return availableRanges;
        }

        // Track current available start time
        LocalTime currentStart = horaireStart;

        for (RendezVous appointment : appointments) {
            LocalTime appointmentStart = appointment.getDateHeureDebut().toLocalTime();
            LocalTime appointmentEnd = appointment.getDateHeureFin().toLocalTime();

            // Ensure appointment times are within horaire bounds
            if (appointmentEnd.isBefore(horaireStart) || appointmentStart.isAfter(horaireEnd)) {
                continue; // Appointment outside horaire range
            }

            // Clamp appointment times to horaire bounds
            appointmentStart = appointmentStart.isBefore(horaireStart) ? horaireStart : appointmentStart;
            appointmentEnd = appointmentEnd.isAfter(horaireEnd) ? horaireEnd : appointmentEnd;

            // If there's a gap before this appointment
            if (currentStart.isBefore(appointmentStart)) {
                availableRanges.add(new AvailableHoraireDTO(
                        horaire.getIdHoraire(),
                        horaire.getJoursSemaine(),
                        currentStart.toString(),
                        appointmentStart.toString(),
                        horaire.getStatus(),
                        horaire.getMedecin().getId(),
                        horaire.getMedecin().getNom(),
                        true // Partially booked
                ));
            }

            // Move current start to end of appointment
            currentStart = appointmentEnd.isAfter(currentStart) ? appointmentEnd : currentStart;
        }

        // Check if there's time remaining after last appointment
        if (currentStart.isBefore(horaireEnd)) {
            availableRanges.add(new AvailableHoraireDTO(
                    horaire.getIdHoraire(),
                    horaire.getJoursSemaine(),
                    currentStart.toString(),
                    horaireEnd.toString(),
                    horaire.getStatus(),
                    horaire.getMedecin().getId(),
                    horaire.getMedecin().getNom(),
                    true // Partially booked
            ));
        }

        return availableRanges;
    }

    @Override
    public Horaire updateHoraire(Long id, Horaire horaire) {
        Horaire existing = horaireRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Horaire non trouvé avec l'ID: " + id));

        existing.setJoursSemaine(horaire.getJoursSemaine());
        existing.setHeureDebut(horaire.getHeureDebut());
        existing.setHeureFin(horaire.getHeureFin());
        existing.setStatus(horaire.getStatus());

        if (horaire.getMedecin() != null) {
            existing.setMedecin(horaire.getMedecin());
        }

        return horaireRepo.save(existing);
    }

    @Override
    public void deleteHoraire(Long id) {
        if (!horaireRepo.existsById(id)) {
            throw new IllegalArgumentException("Horaire non trouvé avec l'ID: " + id);
        }
        horaireRepo.deleteById(id);
    }
}