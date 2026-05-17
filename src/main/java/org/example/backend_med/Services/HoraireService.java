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

            if (horaire.getDate() == null) {
                throw new IllegalArgumentException("Date is required");
            }
            if (horaire.getHeureDebut() == null || horaire.getHeureFin() == null) {
                throw new IllegalArgumentException("HeureDebut and HeureFin are required");
            }
            if (horaire.getHeureDebut().isAfter(horaire.getHeureFin())) {
                throw new IllegalArgumentException("HeureDebut must be before HeureFin");
            }
            if (horaire.getMedecin() == null || horaire.getMedecin().getId() == null) {
                throw new IllegalArgumentException("Medecin is required");
            }

            Horaire toSave;

            if (horaire.getIdHoraire() != null) {
                toSave = horaireRepo.findById(horaire.getIdHoraire())
                        .orElseThrow(() -> new IllegalArgumentException("Horaire not found"));
            } else {
                List<Horaire> existing = horaireRepo.findByMedecinIdAndDate(
                        horaire.getMedecin().getId(),
                        horaire.getDate()
                );
                toSave = existing.isEmpty() ? new Horaire() : existing.get(0);
            }

            boolean wasActive = "ACTIVE".equalsIgnoreCase(toSave.getStatus());
            boolean willBeInactive = "INACTIVE".equalsIgnoreCase(horaire.getStatus());

            toSave.setDate(horaire.getDate());
            toSave.setHeureDebut(horaire.getHeureDebut());
            toSave.setHeureFin(horaire.getHeureFin());
            toSave.setStatus(horaire.getStatus());
            toSave.setMedecin(horaire.getMedecin());

            Horaire saved = horaireRepo.save(toSave);

            if (wasActive && willBeInactive && saved.getIdHoraire() != null) {
                cancelRendezVousForHoraire(saved.getIdHoraire());
            }

            savedHoraires.add(saved);
        }

        return savedHoraires;
    }

    private void cancelRendezVousForHoraire(Long idHoraire) {
        List<RendezVous> rendezVousList = rendezVousRepo.findByHoraireId(idHoraire);
        for (RendezVous rdv : rendezVousList) {
            if (!"ANNULÉ".equalsIgnoreCase(rdv.getStatus())) {
                rdv.setStatus("ANNULÉ");
                rendezVousRepo.save(rdv);
            }
        }
    }

    @Override
    public Horaire updateHoraire(Long id, Horaire horaire) {
        Horaire existing = horaireRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Horaire non trouvé avec l'ID: " + id));

        boolean wasActive = "ACTIVE".equalsIgnoreCase(existing.getStatus());
        boolean willBeInactive = "INACTIVE".equalsIgnoreCase(horaire.getStatus());

        existing.setDate(horaire.getDate());
        existing.setHeureDebut(horaire.getHeureDebut());
        existing.setHeureFin(horaire.getHeureFin());
        existing.setStatus(horaire.getStatus());

        if (horaire.getMedecin() != null) {
            existing.setMedecin(horaire.getMedecin());
        }

        Horaire saved = horaireRepo.save(existing);

        if (wasActive && willBeInactive) {
            cancelRendezVousForHoraire(saved.getIdHoraire());
        }

        return saved;
    }

    @Override
    public void deleteHoraire(Long id) {
        if (!horaireRepo.existsById(id)) {
            throw new IllegalArgumentException("Horaire non trouvé avec l'ID: " + id);
        }
        cancelRendezVousForHoraire(id);
        horaireRepo.deleteById(id);
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
        LocalDate today = LocalDate.now();
        List<AvailableHoraireDTO> result = new ArrayList<>();

        for (Horaire horaire : activeHoraires) {
            if (horaire.getDate() == null) continue;
            if (horaire.getDate().isBefore(today)) continue;

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