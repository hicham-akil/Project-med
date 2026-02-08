package org.example.backend_med.Services;

import lombok.RequiredArgsConstructor;
import org.example.backend_med.Models.Medecin;
import org.example.backend_med.Models.Specialite;
import org.example.backend_med.Repository.MedecinRepo;
import org.example.backend_med.Repository.SpecialiteRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MedecinService implements IMedecin {

    private final MedecinRepo medecinRepo;
    private final SpecialiteRepo specialiteRepo;

    @Override
    public Medecin createMedecin(Medecin medecin) {
        if (medecinRepo.existsByEmail(medecin.getEmail())) {
            throw new IllegalArgumentException("Un médecin avec cet email existe déjà");
        }

        if (medecin.getTelephone() != null && medecinRepo.existsByTelephone(medecin.getTelephone())) {
            throw new IllegalArgumentException("Un médecin avec ce numéro de téléphone existe déjà");
        }

        // Validate and attach existing specialites
        if (medecin.getSpecialites() != null && !medecin.getSpecialites().isEmpty()) {
            List<Specialite> managedSpecialites = new ArrayList<>();
            for (Specialite spec : medecin.getSpecialites()) {
                if (spec.getId() == null) {
                    throw new IllegalArgumentException("Specialite ID ne peut pas être null");
                }
                Specialite existingSpec = specialiteRepo.findById(spec.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Specialite non trouvée avec l'ID: " + spec.getId()));
                managedSpecialites.add(existingSpec);
            }
            medecin.setSpecialites(managedSpecialites);
        }

        // Set bidirectional relationship for horaires
        if (medecin.getHorairesDisponibles() != null) {
            medecin.getHorairesDisponibles().forEach(horaire -> horaire.setMedecin(medecin));
        }

        // Set bidirectional relationship for rendezVous
        if (medecin.getRendezVous() != null) {
            medecin.getRendezVous().forEach(rv -> rv.setMedecin(medecin));
        }

        return medecinRepo.save(medecin);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Medecin> getMedecinById(Long id) {
        return medecinRepo.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medecin> getAllMedecins() {
        return medecinRepo.findAllByOrderByNomAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medecin> getMedecinsBySpecialite(String specialite) {
        return medecinRepo.findBySpecialiteName(specialite);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Medecin> getMedecinByEmail(String email) {
        return medecinRepo.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Medecin> getMedecinByTelephone(String telephone) {
        return medecinRepo.findByTelephone(telephone);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medecin> getAvailableMedecins() {
        return medecinRepo.findMedecinsWithAvailableHoraires();
    }

    // New method: Get available medecins by day
    @Transactional(readOnly = true)
    public List<Medecin> getAvailableMedecinsByDay(String jour) {
        return medecinRepo.findAvailableMedecinsByDay(jour);
    }

    // New method: Get available medecins by day and specialite
    @Transactional(readOnly = true)
    public List<Medecin> getAvailableMedecinsByDayAndSpecialite(String jour, Long specialiteId) {
        return medecinRepo.findAvailableMedecinsByDayAndSpecialite(jour, specialiteId);
    }

    // New method: Get available medecins by day and time
    @Transactional(readOnly = true)
    public List<Medecin> getAvailableMedecinsByDayAndTime(String jour, String heure) {
        return medecinRepo.findAvailableMedecinsByDayAndTime(jour, heure);
    }

    // New method: Get available medecins by day, time and specialite
    @Transactional(readOnly = true)
    public List<Medecin> getAvailableMedecinsByDayTimeAndSpecialite(String jour, String heure, Long specialiteId) {
        return medecinRepo.findAvailableMedecinsByDayTimeAndSpecialite(jour, heure, specialiteId);
    }

    // New method: Get available medecins by specialite only
    @Transactional(readOnly = true)
    public List<Medecin> getAvailableMedecinsBySpecialite(Long specialiteId) {
        return medecinRepo.findAvailableMedecinsBySpecialite(specialiteId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medecin> searchMedecinsByName(String name) {
        return medecinRepo.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(name, name);
    }

    @Override
    public Medecin updateMedecin(Long id, Medecin medecin) {
        Medecin existingMedecin = medecinRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médecin non trouvé avec l'ID: " + id));

        // Check email uniqueness if email is being changed
        if (!existingMedecin.getEmail().equals(medecin.getEmail())
                && medecinRepo.existsByEmail(medecin.getEmail())) {
            throw new IllegalArgumentException("Un médecin avec cet email existe déjà");
        }

        // Check telephone uniqueness if telephone is being changed
        if (medecin.getTelephone() != null
                && !medecin.getTelephone().equals(existingMedecin.getTelephone())
                && medecinRepo.existsByTelephone(medecin.getTelephone())) {
            throw new IllegalArgumentException("Un médecin avec ce numéro de téléphone existe déjà");
        }

        // Update fields
        existingMedecin.setNom(medecin.getNom());
        existingMedecin.setPrenom(medecin.getPrenom());
        existingMedecin.setEmail(medecin.getEmail());
        existingMedecin.setTelephone(medecin.getTelephone());
        existingMedecin.setAdresse(medecin.getAdresse());

        if (medecin.getSpecialites() != null) {
            existingMedecin.setSpecialites(medecin.getSpecialites());
        }

        return medecinRepo.save(existingMedecin);
    }

    @Override
    public Medecin updateMedecinAvailability(Long id, boolean isAvailable) {
        Medecin medecin = medecinRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médecin non trouvé avec l'ID: " + id));

        // Update all horaires status
        if (medecin.getHorairesDisponibles() != null) {
            medecin.getHorairesDisponibles().forEach(horaire -> {
                horaire.setStatus(isAvailable ? "disponible" : "indisponible");
            });
        }

        return medecinRepo.save(medecin);
    }

    @Override
    public void deleteMedecin(Long id) {
        if (!medecinRepo.existsById(id)) {
            throw new IllegalArgumentException("Médecin non trouvé avec l'ID: " + id);
        }
        medecinRepo.deleteById(id);
    }

    @Override
    public Medecin deactivateMedecin(Long id) {
        Medecin medecin = medecinRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médecin non trouvé avec l'ID: " + id));

        // Set all horaires to indisponible
        if (medecin.getHorairesDisponibles() != null) {
            medecin.getHorairesDisponibles().forEach(horaire -> {
                horaire.setStatus("indisponible");
            });
        }

        return medecinRepo.save(medecin);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return medecinRepo.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return medecinRepo.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public long countMedecins() {
        return medecinRepo.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countMedecinsBySpecialite(String specialite) {
        return medecinRepo.countBySpecialiteName(specialite);
    }
}