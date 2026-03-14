package org.example.backend_med.Services;

import org.example.backend_med.Dto.AvailableHoraireDTO;
import org.example.backend_med.Models.Horaire;
import org.example.backend_med.Repository.HoraireRepo;
import org.example.backend_med.Repository.RendezVousRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

@Service
@Transactional
public class HoraireService implements IHoraire {

    @Autowired
    private HoraireRepo horaireRepo;

    @Autowired
    private RendezVousRepo rendezVousRepo;

    // ── CREATE / UPDATE ──────────────────────────────────────────
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

    // ── READ ─────────────────────────────────────────────────────
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

    // ── QUEUE-COMPATIBLE: available slots (just returns active days) ──
    @Override
    @Transactional(readOnly = true)
    public List<AvailableHoraireDTO> getAvailableHorairesWithSlots(Long medecinId) {
        List<Horaire> activeHoraires = horaireRepo.findAvailableHorairesByMedecinId(medecinId);
        LocalDate today = LocalDate.now();
        List<AvailableHoraireDTO> result = new ArrayList<>();

        for (Horaire horaire : activeHoraires) {
            if (horaire.getDate() == null) continue;
            if (horaire.getDate().isBefore(today)) continue;

            // Count patients already in queue for this doctor on this day
            long count = rendezVousRepo.countByMedecinAndDate(medecinId, horaire.getDate());

            result.add(new AvailableHoraireDTO(
                    horaire.getIdHoraire(),
                    horaire.getDate(),
                    horaire.getHeureDebut(),
                    horaire.getHeureFin(),
                    horaire.getStatus(),
                    horaire.getMedecin().getId(),
                    horaire.getMedecin().getNom(),
                    count > 0  // partiallyBooked = true if queue already has patients
            ));
        }

        result.sort(Comparator.comparing(AvailableHoraireDTO::getDate));
        return result;
    }

    // ── QUEUE-COMPATIBLE: available slots for a specific date ──
    @Transactional(readOnly = true)
    public List<AvailableHoraireDTO> getAvailableTimeForDoctorOnDate(Long medecinId, LocalDate date) {
        List<Horaire> activeHoraires = horaireRepo.findAvailableHorairesByMedecinId(medecinId);
        List<AvailableHoraireDTO> result = new ArrayList<>();

        for (Horaire horaire : activeHoraires) {
            if (horaire.getDate() == null) continue;
            if (!horaire.getDate().equals(date)) continue;

            long count = rendezVousRepo.countByMedecinAndDate(medecinId, horaire.getDate());

            result.add(new AvailableHoraireDTO(
                    horaire.getIdHoraire(),
                    horaire.getDate(),
                    horaire.getHeureDebut(),
                    horaire.getHeureFin(),
                    horaire.getStatus(),
                    horaire.getMedecin().getId(),
                    horaire.getMedecin().getNom(),
                    count > 0
            ));
        }

        result.sort(Comparator.comparing(AvailableHoraireDTO::getDate));
        return result;
    }

}