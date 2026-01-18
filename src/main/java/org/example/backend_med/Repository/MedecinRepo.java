package org.example.backend_med.Repository;

import org.example.backend_med.Models.Medecin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedecinRepo extends JpaRepository<Medecin, Long> {

    // Find medecin by email (inherited from Utilisateur)
    Optional<Medecin> findByEmail(String email);

    // Find medecins by telephone
    Optional<Medecin> findByTelephone(String telephone);

    // Find medecins by adresse (partial match)
    List<Medecin> findByAdresseContainingIgnoreCase(String adresse);

    // Find medecins by specialite entity ID
    @Query("SELECT m FROM Medecin m JOIN m.specialites s WHERE s.id = :specialiteId")
    List<Medecin> findBySpecialiteId(@Param("specialiteId") Long specialiteId);

    // Find medecins by specialite name
    @Query("SELECT m FROM Medecin m JOIN m.specialites s WHERE s.nomspecialite = :nomSpecialite")
    List<Medecin> findBySpecialiteName(@Param("nomSpecialite") String nomSpecialite);

    // Find all medecins with available horaires (any day)
    @Query("SELECT DISTINCT m FROM Medecin m JOIN m.horairesDisponibles h WHERE h.status = 'disponible'")
    List<Medecin> findMedecinsWithAvailableHoraires();

    // Find available medecins by specific day
    @Query("SELECT DISTINCT m FROM Medecin m JOIN m.horairesDisponibles h WHERE h.joursSemaine = :jour AND h.status = 'disponible'")
    List<Medecin> findAvailableMedecinsByDay(@Param("jour") String jour);

    // Find available medecins by day and specialite ID
    @Query("SELECT DISTINCT m FROM Medecin m JOIN m.horairesDisponibles h JOIN m.specialites s WHERE h.joursSemaine = :jour AND h.status = 'disponible' AND s.id = :specialiteId")
    List<Medecin> findAvailableMedecinsByDayAndSpecialite(@Param("jour") String jour, @Param("specialiteId") Long specialiteId);

    // Find available medecins by day and specialite name
    @Query("SELECT DISTINCT m FROM Medecin m JOIN m.horairesDisponibles h JOIN m.specialites s WHERE h.joursSemaine = :jour AND h.status = 'disponible' AND s.nomspecialite = :nomSpecialite")
    List<Medecin> findAvailableMedecinsByDayAndSpecialiteName(@Param("jour") String jour, @Param("nomSpecialite") String nomSpecialite);

    // Find available medecins by specialite only
    @Query("SELECT DISTINCT m FROM Medecin m JOIN m.horairesDisponibles h JOIN m.specialites s WHERE h.status = 'disponible' AND s.id = :specialiteId")
    List<Medecin> findAvailableMedecinsBySpecialite(@Param("specialiteId") Long specialiteId);

    // Find available medecins by day and time range
    @Query("SELECT DISTINCT m FROM Medecin m JOIN m.horairesDisponibles h WHERE h.joursSemaine = :jour AND h.status = 'disponible' AND h.heureDebut <= :heureRecherche AND h.heureFin >= :heureRecherche")
    List<Medecin> findAvailableMedecinsByDayAndTime(@Param("jour") String jour, @Param("heureRecherche") String heureRecherche);

    // Find available medecins by day, time and specialite
    @Query("SELECT DISTINCT m FROM Medecin m JOIN m.horairesDisponibles h JOIN m.specialites s WHERE h.joursSemaine = :jour AND h.status = 'disponible' AND h.heureDebut <= :heureRecherche AND h.heureFin >= :heureRecherche AND s.id = :specialiteId")
    List<Medecin> findAvailableMedecinsByDayTimeAndSpecialite(@Param("jour") String jour, @Param("heureRecherche") String heureRecherche, @Param("specialiteId") Long specialiteId);

    // Find medecins by nom or prenom (inherited from Utilisateur)
    List<Medecin> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);

    // Count medecins by specialite ID
    @Query("SELECT COUNT(m) FROM Medecin m JOIN m.specialites s WHERE s.id = :specialiteId")
    Long countBySpecialiteId(@Param("specialiteId") Long specialiteId);

    // Count medecins by specialite name
    @Query("SELECT COUNT(m) FROM Medecin m JOIN m.specialites s WHERE s.nomspecialite = :nomSpecialite")
    Long countBySpecialiteName(@Param("nomSpecialite") String nomSpecialite);

    // Find medecins with rendez-vous count
    @Query("SELECT m FROM Medecin m LEFT JOIN m.rendezVous r GROUP BY m HAVING COUNT(r) > :minRendezVous")
    List<Medecin> findMedecinsWithMinimumRendezVous(@Param("minRendezVous") Long minRendezVous);

    // Check if medecin exists by email
    boolean existsByEmail(String email);

    // Check if medecin exists by telephone
    boolean existsByTelephone(String telephone);

    // Find all medecins ordered by nom
    List<Medecin> findAllByOrderByNomAsc();

    // Find medecins by multiple specialites (names)
    @Query("SELECT DISTINCT m FROM Medecin m JOIN m.specialites s WHERE s.nomspecialite IN :specialites")
    List<Medecin> findBySpecialiteNames(@Param("specialites") List<String> specialites);

    // Find medecins by multiple specialites (IDs)
    @Query("SELECT DISTINCT m FROM Medecin m JOIN m.specialites s WHERE s.id IN :specialiteIds")
    List<Medecin> findBySpecialiteIds(@Param("specialiteIds") List<Long> specialiteIds);
}