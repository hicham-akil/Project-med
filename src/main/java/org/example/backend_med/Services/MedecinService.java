package org.example.backend_med.Services;

import lombok.RequiredArgsConstructor;
import org.example.backend_med.Models.Medecin;
import org.example.backend_med.Repository.MedecinRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MedecinService implements IMedecin {
    @Autowired
    private MedecinRepo medecinRepo;

    @Override
    public Medecin createMedecin(Medecin medecin) {
        if (medecinRepo.existsByEmail(medecin.getEmail())) {
            throw new IllegalArgumentException("Un médecin avec cet email existe déjà");
        }

        if (medecin.getTelephone() != null && medecinRepo.existsByTelephone(medecin.getTelephone())) {
            throw new IllegalArgumentException("Un médecin avec ce numéro de téléphone existe déjà");
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
        return medecinRepo.findBySpecialite(specialite);
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

    @Override
    @Transactional(readOnly = true)
    public List<Medecin> searchMedecinsByName(String name) {
        return medecinRepo.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(name, name);
    }

    @Override
    public Medecin updateMedecin(Long id, Medecin medecin) {
        Medecin existingMedecin = medecinRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médecin non trouvé avec l'ID: " + id));

        if (!existingMedecin.getEmail().equals(medecin.getEmail())
                && medecinRepo.existsByEmail(medecin.getEmail())) {
            throw new IllegalArgumentException("Un médecin avec cet email existe déjà");
        }

        if (medecin.getTelephone() != null
                && !medecin.getTelephone().equals(existingMedecin.getTelephone())
                && medecinRepo.existsByTelephone(medecin.getTelephone())) {
            throw new IllegalArgumentException("Un médecin avec ce numéro de téléphone existe déjà");
        }

        existingMedecin.setNom(medecin.getNom());
        existingMedecin.setPrenom(medecin.getPrenom());
        existingMedecin.setEmail(medecin.getEmail());
        existingMedecin.setSpecialite(medecin.getSpecialite());
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
        return medecinRepo.findBySpecialite(specialite).size();
    }
}