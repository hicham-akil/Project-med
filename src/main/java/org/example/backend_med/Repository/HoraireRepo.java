package org.example.backend_med.Repository;

import org.example.backend_med.Models.Horaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoraireRepo extends JpaRepository<Horaire, Long> {

    // Find by medecin

    // Find by jour
    List<Horaire> findByJoursSemaine(String joursSemaine);

    // Find by status
    List<Horaire> findByStatus(String status);

    @Query("SELECT h FROM Horaire h WHERE h.medecin.id = :medecinId AND h.joursSemaine = :joursSemaine")
    List<Horaire> findByMedecinIdAndJoursSemaine(
            @Param("medecinId") Long medecinId,
            @Param("joursSemaine") String joursSemaine
    );


    @Query("SELECT h FROM Horaire h WHERE h.medecin.id = :medecinId")
    List<Horaire> findAllByMedecinId(@Param("medecinId") Long medecinId);
}