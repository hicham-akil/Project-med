package org.example.backend_med.Services;

import org.example.backend_med.Models.Administrateur;

import java.util.List;
import java.util.Optional;

public interface IAdministrateur {

    // Create
    Administrateur createAdministrateur(Administrateur administrateur);

    // Read
    Optional<Administrateur> getAdministrateurById(Long id);
    List<Administrateur> getAllAdministrateurs();
    Optional<Administrateur> getAdministrateurByEmail(String email);
    List<Administrateur> getAdministrateursByRole(String role);
    List<Administrateur> searchAdministrateursByName(String name);

    // Update
    Administrateur updateAdministrateur(Long id, Administrateur administrateur);

    // Delete
    void deleteAdministrateur(Long id);

    // Utility
    boolean existsById(Long id);
    boolean existsByEmail(String email);
    long countAdministrateurs();
}