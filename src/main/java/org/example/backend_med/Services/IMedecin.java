package org.example.backend_med.Services;

import org.example.backend_med.Models.Medecin;

import java.util.List;
import java.util.Optional;

public interface IMedecin {
    Medecin createMedecin(Medecin medecin);
    Optional<Medecin> getMedecinById(Long id);
    List<Medecin> getAllMedecins();
    List<Medecin> getMedecinsBySpecialite(String specialite);
    Optional<Medecin> getMedecinByEmail(String email);
    Optional<Medecin> getMedecinByTelephone(String telephone);
    List<Medecin> getAvailableMedecins();
    List<Medecin> searchMedecinsByName(String name);
    Medecin updateMedecin(Long id, Medecin medecin);
    Medecin updateMedecinAvailability(Long id, boolean isAvailable);
    void deleteMedecin(Long id);
    Medecin deactivateMedecin(Long id);
    boolean existsById(Long id);
    boolean existsByEmail(String email);
    long countMedecins();
    long countMedecinsBySpecialite(String specialite);

    // ADD THESE NEW METHODS:
    List<Medecin> getAvailableMedecinsByDay(String jour);
    List<Medecin> getAvailableMedecinsByDayAndSpecialite(String jour, Long specialiteId);
    List<Medecin> getAvailableMedecinsByDayAndTime(String jour, String heure);
    List<Medecin> getAvailableMedecinsByDayTimeAndSpecialite(String jour, String heure, Long specialiteId);
    List<Medecin> getAvailableMedecinsBySpecialite(Long specialiteId);
}