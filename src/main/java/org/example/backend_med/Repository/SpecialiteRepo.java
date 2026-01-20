package org.example.backend_med.Repository;

import org.example.backend_med.Models.Specialite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpecialiteRepo extends JpaRepository<Specialite, Long> {

    // Find by name
    Optional<Specialite> findByNomspecialite(String nomspecialite);

    // Check if exists by name
    boolean existsByNomspecialite(String nomspecialite);
}