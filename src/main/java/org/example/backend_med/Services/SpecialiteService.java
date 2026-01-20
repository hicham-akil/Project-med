
package org.example.backend_med.Services;

import lombok.RequiredArgsConstructor;
import org.example.backend_med.Models.Specialite;
import org.example.backend_med.Repository.SpecialiteRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SpecialiteService implements ISpecialite {

    private final SpecialiteRepo specialiteRepo;

    @Override
    public Specialite createSpecialite(Specialite specialite) {
        // Validate name uniqueness
        if (specialiteRepo.existsByNomspecialite(specialite.getNomspecialite())) {
            throw new IllegalArgumentException("Une spécialité avec ce nom existe déjà");
        }
        return specialiteRepo.save(specialite);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Specialite> getSpecialiteById(Long id) {
        return specialiteRepo.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Specialite> getAllSpecialites() {
        return specialiteRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Specialite> getSpecialiteByName(String nomspecialite) {
        return specialiteRepo.findByNomspecialite(nomspecialite);
    }

    @Override
    public Specialite updateSpecialite(Long id, Specialite specialite) {
        Specialite existing = specialiteRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Spécialité non trouvée avec l'ID: " + id));

        // Check name uniqueness if changed
        if (!existing.getNomspecialite().equals(specialite.getNomspecialite())
                && specialiteRepo.existsByNomspecialite(specialite.getNomspecialite())) {
            throw new IllegalArgumentException("Une spécialité avec ce nom existe déjà");
        }

        existing.setNomspecialite(specialite.getNomspecialite());
        return specialiteRepo.save(existing);
    }

    @Override
    public void deleteSpecialite(Long id) {
        if (!specialiteRepo.existsById(id)) {
            throw new IllegalArgumentException("Spécialité non trouvée avec l'ID: " + id);
        }
        specialiteRepo.deleteById(id);
    }
}