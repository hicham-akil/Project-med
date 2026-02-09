package org.example.backend_med.Services;

import org.example.backend_med.Models.Horaire;
import org.example.backend_med.Repository.HoraireRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HoraireService implements IHoraire {

    @Autowired
    private HoraireRepo horaireRepo;

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