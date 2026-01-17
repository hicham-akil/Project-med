package org.example.backend_med.Services;

import org.example.backend_med.Models.Administrateur;

import java.util.List;
import java.util.Optional;

public class AdministrateurService implements IAdministrateur{

    @Override
    public Administrateur createAdministrateur(Administrateur administrateur) {
        return null;
    }

    @Override
    public Optional<Administrateur> getAdministrateurById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Administrateur> getAllAdministrateurs() {
        return List.of();
    }

    @Override
    public Optional<Administrateur> getAdministrateurByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Administrateur updateAdministrateur(Long id, Administrateur administrateur) {
        return null;
    }

    @Override
    public void deleteAdministrateur(Long id) {

    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public long countAdministrateurs() {
        return 0;
    }
}
