package org.example.backend_med.Services;

import org.example.backend_med.Models.Medecin;

import java.util.List;
import java.util.Optional;

public class MedecinService implements IMedecin{
    @Override
    public Medecin createMedecin(Medecin medecin) {
        return null;
    }

    @Override
    public Optional<Medecin> getMedecinById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Medecin> getAllMedecins() {
        return List.of();
    }

    @Override
    public List<Medecin> getMedecinsBySpecialite(String specialite) {
        return List.of();
    }

    @Override
    public Optional<Medecin> getMedecinByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<Medecin> getMedecinByTelephone(String telephone) {
        return Optional.empty();
    }

    @Override
    public List<Medecin> getAvailableMedecins() {
        return List.of();
    }

    @Override
    public List<Medecin> searchMedecinsByName(String name) {
        return List.of();
    }

    @Override
    public Medecin updateMedecin(Long id, Medecin medecin) {
        return null;
    }

    @Override
    public Medecin updateMedecinAvailability(Long id, boolean isAvailable) {
        return null;
    }

    @Override
    public void deleteMedecin(Long id) {

    }

    @Override
    public Medecin deactivateMedecin(Long id) {
        return null;
    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    public long countMedecins() {
        return 0;
    }

    @Override
    public long countMedecinsBySpecialite(String specialite) {
        return 0;
    }
}
