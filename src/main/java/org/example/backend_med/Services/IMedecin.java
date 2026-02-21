package org.example.backend_med.Services;

import org.example.backend_med.Models.Medecin;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IMedecin {

    Medecin createMedecin(Medecin medecin);

    Optional<Medecin> getMedecinById(Long id);

    List<Medecin> getAllMedecins();

    List<Medecin> getMedecinsBySpecialite(Long specialiteId);

    Optional<Medecin> getMedecinByEmail(String email);

    Optional<Medecin> getMedecinByTelephone(String telephone);

    List<Medecin> getAvailableMedecins();

    // ✅ Replaced getAvailableMedecinsByDay(String jour)
    List<Medecin> getAvailableMedecinsByDate(LocalDate date);

    // ✅ Replaced getAvailableMedecinsByDayAndSpecialite(String jour, Long specialiteId)
    List<Medecin> getAvailableMedecinsByDateAndSpecialite(LocalDate date, Long specialiteId);

    // ✅ Replaced getAvailableMedecinsByDayAndTime(String jour, String heure)
    List<Medecin> getAvailableMedecinsByDateAndTime(LocalDate date, String heure);

    // ✅ Replaced getAvailableMedecinsByDayTimeAndSpecialite(String jour, String heure, Long specialiteId)
    List<Medecin> getAvailableMedecinsByDateTimeAndSpecialite(LocalDate date, String heure, Long specialiteId);

    List<Medecin> getAvailableMedecinsBySpecialite(Long specialiteId);

    List<Medecin> searchMedecinsByName(String name);

    Medecin updateMedecin(Long id, Medecin medecin);

    Medecin updateMedecinAvailability(Long id, boolean isAvailable);

    Medecin deactivateMedecin(Long id);

    void deleteMedecin(Long id);

    boolean existsById(Long id);

    boolean existsByEmail(String email);

    long countMedecins();

    long countMedecinsBySpecialite(String specialite);
}