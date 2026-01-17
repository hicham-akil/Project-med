package org.example.backend_med.Services;

import org.example.backend_med.Models.Administrateur;
import java.util.List;
import java.util.Optional;

public interface IAdministrateur {

    // Create
    Administrateur createAdministrateur(Administrateur administrateur);

    // Read - Single
    Optional<Administrateur> getAdministrateurById(Long id);

    // Read - All
    List<Administrateur> getAllAdministrateurs();

    // Read - By Email (common use case)
    Optional<Administrateur> getAdministrateurByEmail(String email);

    // Update
    Administrateur updateAdministrateur(Long id, Administrateur administrateur);

    // Delete
    void deleteAdministrateur(Long id);

    // Check existence
    boolean existsById(Long id);

    // Count
    long countAdministrateurs();
}