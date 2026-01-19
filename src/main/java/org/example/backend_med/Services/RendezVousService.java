package org.example.backend_med.Services;

import lombok.RequiredArgsConstructor;
import org.example.backend_med.Models.RendezVous;
import org.example.backend_med.Repository.RendezVousRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RendezVousService implements IRendezVous {
    @Autowired
    private RendezVousRepo rendezVousRepo;

    @Override
    public RendezVous createRendezVous(RendezVous rendezVous) {
        // Validate that the time slot is available
        if (!isTimeSlotAvailable(rendezVous.getMedecin().getId(), rendezVous.getDateHeure())) {
            throw new IllegalArgumentException("Ce créneau horaire n'est pas disponible");
        }

        // Set default status if not provided
        if (rendezVous.getStatus() == null || rendezVous.getStatus().isEmpty()) {
            rendezVous.setStatus("en attente");
        }

        // Validate date is not in the past
        if (rendezVous.getDateHeure().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La date du rendez-vous ne peut pas être dans le passé");
        }

        return rendezVousRepo.save(rendezVous);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RendezVous> getRendezVousById(Long id) {
        return rendezVousRepo.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> getAllRendezVous() {
        return rendezVousRepo.findAllByOrderByDateHeureDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> getRendezVousByPatientId(Long patientId) {
        return rendezVousRepo.findByPatientId(patientId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> getRendezVousByMedecinId(Long medecinId) {
        return rendezVousRepo.findByMedecinId(medecinId);
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
        if (!existing.getDateHeure().equals(rendezVous.getDateHeure())) {
            if (!isTimeSlotAvailable(rendezVous.getMedecin().getId(), rendezVous.getDateHeure())) {
                throw new IllegalArgumentException("Ce créneau horaire n'est pas disponible");
            }
        }

        existing.setDateHeure(rendezVous.getDateHeure());
        existing.setMotif(rendezVous.getMotif());
        existing.setStatus(rendezVous.getStatus());

        if (rendezVous.getMedecin() != null) {
            existing.setMedecin(rendezVous.getMedecin());
        }

        if (rendezVous.getPatient() != null) {
            existing.setPatient(rendezVous.getPatient());
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
    public RendezVous rescheduleRendezVous(Long id, LocalDateTime newDateTime) {
        RendezVous rendezVous = rendezVousRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous non trouvé avec l'ID: " + id));

        // Validate new date is not in the past
        if (newDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La nouvelle date ne peut pas être dans le passé");
        }

        // Check if new time slot is available
        if (!isTimeSlotAvailable(rendezVous.getMedecin().getId(), newDateTime)) {
            throw new IllegalArgumentException("Ce créneau horaire n'est pas disponible");
        }

        rendezVous.setDateHeure(newDateTime);
        rendezVous.setStatus("reporté");

        return rendezVousRepo.save(rendezVous);
    }

    @Override
    public RendezVous confirmRendezVous(Long id) {
        RendezVous rendezVous = rendezVousRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous non trouvé avec l'ID: " + id));

        rendezVous.setStatus("confirmé");
        return rendezVousRepo.save(rendezVous);
    }

    @Override
    public RendezVous cancelRendezVous(Long id) {
        RendezVous rendezVous = rendezVousRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous non trouvé avec l'ID: " + id));

        rendezVous.setStatus("annulé");
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
        // Returns true if the slot is available (no existing appointment)
        return !rendezVousRepo.existsByMedecinAndDateTime(medecinId, dateTime);
    }

    @Override
    @Transactional(readOnly = true)
    public long countRendezVousByMedecinAndDate(Long medecinId, LocalDate date) {
        return rendezVousRepo.countByMedecinAndDate(medecinId, date);
    }
}