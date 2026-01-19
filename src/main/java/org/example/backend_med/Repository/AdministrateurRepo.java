package org.example.backend_med.Repository;

import org.example.backend_med.Models.Administrateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdministrateurRepo extends JpaRepository<Administrateur, Long> {

    // Find by email
    Optional<Administrateur> findByEmail(String email);

    // Find by role
    List<Administrateur> findByRole(String role);

    // Find by nom or prenom
    List<Administrateur> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);

    // Check if exists by email
    boolean existsByEmail(String email);

    // Find all ordered by nom
    List<Administrateur> findAllByOrderByNomAsc();
}
