package org.example.backend_med.Repository;

import org.example.backend_med.Models.Utlisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utlisateur, Long> {
    Optional<Utlisateur> findByEmail(String email);
}