package org.example.backend_med.Services;

import lombok.RequiredArgsConstructor;
import org.example.backend_med.Models.Administrateur;
import org.example.backend_med.Repository.AdministrateurRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdministrateurService implements IAdministrateur {
    @Autowired
    private AdministrateurRepo administrateurRepo;

    @Override
    public Administrateur createAdministrateur(Administrateur administrateur) {
        // Validate email uniqueness
        if (administrateurRepo.existsByEmail(administrateur.getEmail())) {
            throw new IllegalArgumentException("Un administrateur avec cet email existe déjà");
        }

        // Set default role if not provided
        if (administrateur.getRole() == null || administrateur.getRole().isEmpty()) {
            administrateur.setRole("admin");
        }

        return administrateurRepo.save(administrateur);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Administrateur> getAdministrateurById(Long id) {
        return administrateurRepo.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Administrateur> getAllAdministrateurs() {
        return administrateurRepo.findAllByOrderByNomAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Administrateur> getAdministrateurByEmail(String email) {
        return administrateurRepo.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Administrateur> getAdministrateursByRole(String role) {
        return administrateurRepo.findByRole(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Administrateur> searchAdministrateursByName(String name) {
        return administrateurRepo.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(name, name);
    }

    @Override
    public Administrateur updateAdministrateur(Long id, Administrateur administrateur) {
        Administrateur existing = administrateurRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Administrateur non trouvé avec l'ID: " + id));

        // Check email uniqueness if changed
        if (!existing.getEmail().equals(administrateur.getEmail())
                && administrateurRepo.existsByEmail(administrateur.getEmail())) {
            throw new IllegalArgumentException("Un administrateur avec cet email existe déjà");
        }

        // Update fields
        existing.setNom(administrateur.getNom());
        existing.setPrenom(administrateur.getPrenom());
        existing.setEmail(administrateur.getEmail());
        existing.setRole(administrateur.getRole());

        return administrateurRepo.save(existing);
    }

    @Override
    public void deleteAdministrateur(Long id) {
        if (!administrateurRepo.existsById(id)) {
            throw new IllegalArgumentException("Administrateur non trouvé avec l'ID: " + id);
        }
        administrateurRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return administrateurRepo.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return administrateurRepo.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public long countAdministrateurs() {
        return administrateurRepo.count();
    }
}