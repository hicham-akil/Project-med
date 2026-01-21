package org.example.backend_med.Repository;

import org.example.backend_med.Models.Horaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoraireRepo extends JpaRepository<Horaire, Long> {

    // Find by medecin
    List<Horaire> findByMedecinId(Long medecinId);

    // Find by jour
    List<Horaire> findByJoursSemaine(String joursSemaine);

    // Find by status
    List<Horaire> findByStatus(String status);

    // Find by medecin and jour
    List<Horaire> findByMedecinIdAndJoursSemaine(Long medecinId, String joursSemaine);
}