package org.example.backend_med.Repository;

import org.example.backend_med.Models.Horaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoraireRepo extends JpaRepository<Horaire, Long> {

    // Find by jour
    List<Horaire> findByJoursSemaine(String joursSemaine);

    // Find by status
    List<Horaire> findByStatus(String status);

    // Find by medecin and jour
    @Query("SELECT h FROM Horaire h WHERE h.medecin.id = :medecinId AND h.joursSemaine = :joursSemaine")
    List<Horaire> findByMedecinIdAndJoursSemaine(
            @Param("medecinId") Long medecinId,
            @Param("joursSemaine") String joursSemaine
    );

    // Find all horaires by medecin
    @Query("SELECT h FROM Horaire h WHERE h.medecin.id = :medecinId")
    List<Horaire> findAllByMedecinId(@Param("medecinId") Long medecinId);

    // Get available horaires (just ACTIVE status)
    @Query("SELECT h FROM Horaire h WHERE h.medecin.id = :medecinId AND h.status = 'ACTIVE'")
    List<Horaire> findAvailableHorairesByMedecinId(@Param("medecinId") Long medecinId);

    // Get horaires that have NO appointments at all (completely free)
    @Query("SELECT h FROM Horaire h WHERE h.medecin.id = :medecinId " +
            "AND h.status = 'ACTIVE' " +
            "AND h.idHoraire NOT IN (SELECT DISTINCT r.horaire.idHoraire FROM RendezVous r WHERE r.horaire IS NOT NULL)")
    List<Horaire> findCompletelyAvailableHorairesByMedecinId(@Param("medecinId") Long medecinId);
}