package org.example.backend_med.Services;

import org.example.backend_med.Models.Medecin;
import java.util.List;
import java.util.Optional;

public interface IMedecin {

    // Create
    Medecin createMedecin(Medecin medecin);

    // Read - Single
    Optional<Medecin> getMedecinById(Long id);

    // Read - All
    List<Medecin> getAllMedecins();

    // Read - By Speciality
    List<Medecin> getMedecinsBySpecialite(String specialite);

    // Read - By Email
    Optional<Medecin> getMedecinByEmail(String email);

    // Read - By Phone
    Optional<Medecin> getMedecinByTelephone(String telephone);

    // Read - Available Doctors
    List<Medecin> getAvailableMedecins();

    // Read - By Name (Search)
    List<Medecin> searchMedecinsByName(String name);

    // Update
    Medecin updateMedecin(Long id, Medecin medecin);

    // Update - Availability Status
    Medecin updateMedecinAvailability(Long id, boolean isAvailable);

    // Delete
    void deleteMedecin(Long id);

    // Soft Delete (if needed)
    Medecin deactivateMedecin(Long id);

    // Check existence
    boolean existsById(Long id);

    // Check by email
    boolean existsByEmail(String email);

    // Count
    long countMedecins();

    // Count by speciality
    long countMedecinsBySpecialite(String specialite);
}