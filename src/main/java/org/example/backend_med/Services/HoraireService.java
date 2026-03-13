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

            if (horaire.getIdHoraire() != null) {
                toSave = horaireRepo.findById(horaire.getIdHoraire())
                        .orElse(new Horaire());
            } else {
                List<Horaire> existing = horaireRepo.findByMedecinIdAndDate(
                        horaire.getMedecin().getId(),
                        horaire.getDate()
                );
                toSave = existing.isEmpty() ? new Horaire() : existing.get(0);
            }

            toSave.setDate(horaire.getDate());
            toSave.setHeureDebut(horaire.getHeureDebut());
            toSave.setHeureFin(horaire.getHeureFin());
            toSave.setStatus(horaire.getStatus());
            toSave.setMedecin(horaire.getMedecin());

            savedHoraires.add(horaireRepo.save(toSave));
        }

        return savedHoraires;
    }

    @Transactional(readOnly = true)
    public List<AvailableHoraireDTO> getAvailableTimeForDoctorOnDate(Long medecinId, LocalDate date) {
        List<Horaire> activeHoraires = horaireRepo.findAvailableHorairesByMedecinId(medecinId);
        List<RendezVous> allAppointments = rendezVousRepo.findByMedecinId(medecinId);

        List<AvailableHoraireDTO> result = new ArrayList<>();

        for (Horaire horaire : activeHoraires) {
            // Only process horaires matching the requested date
            if (!horaire.getDate().equals(date)) continue;

            LocalDateTime startOfDay = horaire.getDate().atStartOfDay();
            LocalDateTime endOfDay = horaire.getDate().atTime(LocalTime.MAX);

            List<RendezVous> dayAppointments = allAppointments.stream()
                    .filter(rdv -> !rdv.getDateHeureDebut().isBefore(startOfDay)
                            && !rdv.getDateHeureDebut().isAfter(endOfDay))
                    .filter(rdv -> !"ANNULE".equals(rdv.getStatus()))
                    .sorted(Comparator.comparing(RendezVous::getDateHeureDebut))
                    .collect(Collectors.toList());

            result.addAll(calculateAvailableTimeRanges(horaire, dayAppointments));
        }

        result.sort(Comparator.comparing(AvailableHoraireDTO::getDate));
        return result;
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
    public List<Horaire> getHorairesByDate(LocalDate date) {
        return horaireRepo.findByDate(date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Horaire> getHorairesByStatus(String status) {
        return horaireRepo.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Horaire> getHorairesByMedecinAndDate(Long medecinId, LocalDate date) {
        return horaireRepo.findByMedecinIdAndDate(medecinId, date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Horaire> getAvailableHorairesByMedecinId(Long medecinId) {
        return horaireRepo.findAvailableHorairesByMedecinId(medecinId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailableHoraireDTO> getAvailableHorairesWithSlots(Long medecinId) {
        List<Horaire> activeHoraires = horaireRepo.findAvailableHorairesByMedecinId(medecinId);
        List<RendezVous> allAppointments = rendezVousRepo.findByMedecinId(medecinId);
        LocalDate today = LocalDate.now();
        List<AvailableHoraireDTO> availableSlots = new ArrayList<>();

        for (Horaire horaire : activeHoraires) {
            if (horaire.getDate() == null) continue;
            if (horaire.getDate().isBefore(today)) continue;
            List<RendezVous> dayAppointments = allAppointments.stream()
                    .filter(rdv -> rdv.getDateHeureDebut().toLocalDate().equals(horaire.getDate()))
                    .filter(rdv -> !"ANNULE".equals(rdv.getStatus()))
                    .sorted(Comparator.comparing(RendezVous::getDateHeureDebut))
                    .collect(Collectors.toList());

            availableSlots.addAll(calculateAvailableTimeRanges(horaire, dayAppointments));
        }
        availableSlots.sort(Comparator.comparing(AvailableHoraireDTO::getDate));

        return availableSlots;
    }
    private List<AvailableHoraireDTO> calculateAvailableTimeRanges(Horaire horaire, List<RendezVous> appointments) {
        List<AvailableHoraireDTO> availableRanges = new ArrayList<>();

        LocalTime horaireStart = LocalTime.parse(horaire.getHeureDebut());
        LocalTime horaireEnd = LocalTime.parse(horaire.getHeureFin());

        if (appointments.isEmpty()) {
            availableRanges.add(new AvailableHoraireDTO(
                    horaire.getIdHoraire(),
                    horaire.getDate(),
                    horaire.getHeureDebut(),
                    horaire.getHeureFin(),
                    horaire.getStatus(),
                    horaire.getMedecin().getId(),
                    horaire.getMedecin().getNom(),
                    false
            ));
            return availableRanges;
        }

        LocalTime currentStart = horaireStart;

        for (RendezVous appointment : appointments) {
            LocalTime appointmentStart = appointment.getDateHeureDebut().toLocalTime();
            LocalTime appointmentEnd = appointment.getDateHeureFin().toLocalTime();

            if (appointmentEnd.isBefore(horaireStart) || appointmentStart.isAfter(horaireEnd)) continue;

            appointmentStart = appointmentStart.isBefore(horaireStart) ? horaireStart : appointmentStart;
            appointmentEnd = appointmentEnd.isAfter(horaireEnd) ? horaireEnd : appointmentEnd;

            if (currentStart.isBefore(appointmentStart)) {
                long minutes = Duration.between(currentStart, appointmentStart).toMinutes();
                // ✅ Only add if at least 60 min (minimum appointment duration)
                if (minutes >= 60) {
                    availableRanges.add(new AvailableHoraireDTO(
                            horaire.getIdHoraire(),
                            horaire.getDate(),
                            currentStart.toString(),
                            appointmentStart.toString(),
                            horaire.getStatus(),
                            horaire.getMedecin().getId(),
                            horaire.getMedecin().getNom(),
                            true
                    ));
                }
            }

            currentStart = appointmentEnd.isAfter(currentStart) ? appointmentEnd : currentStart;
        }

        if (currentStart.isBefore(horaireEnd)) {
            long minutes = Duration.between(currentStart, horaireEnd).toMinutes();
            // ✅ Only add if at least 60 min
            if (minutes >= 60) {
                availableRanges.add(new AvailableHoraireDTO(
                        horaire.getIdHoraire(),
                        horaire.getDate(),
                        currentStart.toString(),
                        horaireEnd.toString(),
                        horaire.getStatus(),
                        horaire.getMedecin().getId(),
                        horaire.getMedecin().getNom(),
                        true
                ));
            }
        }

        return availableRanges;
    }

    @Override
    public Horaire updateHoraire(Long id, Horaire horaire) {
        Horaire existing = horaireRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Horaire non trouvé avec l'ID: " + id));

        existing.setDate(horaire.getDate());
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