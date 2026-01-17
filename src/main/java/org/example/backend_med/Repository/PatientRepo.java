package org.example.backend_med.Repository;

import org.example.backend_med.Models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepo extends JpaRepository<Patient, Long> {

    // Find by unique fields
    Optional<Patient> findByEmail(String email);
    Optional<Patient> findByTelephone(String telephone);

    // Search by name (case-insensitive)
    List<Patient> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);


    // Existence checks
    boolean existsByEmail(String email);

}