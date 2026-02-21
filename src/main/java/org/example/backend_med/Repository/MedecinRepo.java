package org.example.backend_med.Repository;

import org.example.backend_med.Models.Medecin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedecinRepo extends JpaRepository<Medecin, Long> {

    Optional<Medecin> findByEmail(String email);

    Optional<Medecin> findByTelephone(String telephone);

    List<Medecin> findByAdresseContainingIgnoreCase(String adresse);

    @Query("SELECT m FROM Medecin m JOIN m.specialites s WHERE s.id = :specialiteId")
    List<Medecin> findBySpecialiteId(@Param("specialiteId") Long specialiteId);

    @Query("SELECT m FROM Medecin m JOIN m.specialites s WHERE s.nomspecialite = :nomSpecialite")
    List<Medecin> findBySpecialiteName(@Param("nomSpecialite") String nomSpecialite);

    // ✅ Any medecin with an ACTIVE horaire
    @Query("SELECT DISTINCT m FROM Medecin m JOIN m.horairesDisponibles h WHERE h.status = 'ACTIVE'")
    List<Medecin> findMedecinsWithAvailableHoraires();

    // ✅ Available by specific date
    @Query("SELECT DISTINCT m FROM Medecin m JOIN m.horairesDisponibles h " +
            "WHERE h.date = :date AND h.status = 'ACTIVE'")
    List<Medecin> findAvailableMedecinsByDate(@Param("date") LocalDate date);

    // ✅ Available by date and specialite ID
    @Query("SELECT DISTINCT m FROM Medecin m JOIN m.horairesDisponibles h JOIN m.specialites s " +
            "WHERE h.date = :date AND h.status = 'ACTIVE' AND s.id = :specialiteId")
    List<Medecin> findAvailableMedecinsByDateAndSpecialite(
            @Param("date") LocalDate date,
            @Param("specialiteId") Long specialiteId);

    // ✅ Available by date and specialite name
    @Query("SELECT DISTINCT m FROM Medecin m JOIN m.horairesDisponibles h JOIN m.specialites s " +
            "WHERE h.date = :date AND h.status = 'ACTIVE' AND s.nomspecialite = :nomSpecialite")
    List<Medecin> findAvailableMedecinsByDateAndSpecialiteName(
            @Param("date") LocalDate date,
            @Param("nomSpecialite") String nomSpecialite);

    // ✅ Available by specialite only (any date)
    @Query("SELECT DISTINCT m FROM Medecin m JOIN m.horairesDisponibles h JOIN m.specialites s " +
            "WHERE h.status = 'ACTIVE' AND s.id = :specialiteId")
    List<Medecin> findAvailableMedecinsBySpecialite(@Param("specialiteId") Long specialiteId);

    // ✅ Available by date and time range
    @Query("SELECT DISTINCT m FROM Medecin m JOIN m.horairesDisponibles h " +
            "WHERE h.date = :date AND h.status = 'ACTIVE' " +
            "AND h.heureDebut <= :heureRecherche AND h.heureFin >= :heureRecherche")
    List<Medecin> findAvailableMedecinsByDateAndTime(
            @Param("date") LocalDate date,
            @Param("heureRecherche") String heureRecherche);

    // ✅ Available by date, time and specialite
    @Query("SELECT DISTINCT m FROM Medecin m JOIN m.horairesDisponibles h JOIN m.specialites s " +
            "WHERE h.date = :date AND h.status = 'ACTIVE' " +
            "AND h.heureDebut <= :heureRecherche AND h.heureFin >= :heureRecherche " +
            "AND s.id = :specialiteId")
    List<Medecin> findAvailableMedecinsByDateTimeAndSpecialite(
            @Param("date") LocalDate date,
            @Param("heureRecherche") String heureRecherche,
            @Param("specialiteId") Long specialiteId);

    List<Medecin> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);

    @Query("SELECT COUNT(m) FROM Medecin m JOIN m.specialites s WHERE s.id = :specialiteId")
    Long countBySpecialiteId(@Param("specialiteId") Long specialiteId);

    @Query("SELECT COUNT(m) FROM Medecin m JOIN m.specialites s WHERE s.nomspecialite = :nomSpecialite")
    Long countBySpecialiteName(@Param("nomSpecialite") String nomSpecialite);

    @Query("SELECT m FROM Medecin m LEFT JOIN m.rendezVous r GROUP BY m HAVING COUNT(r) > :minRendezVous")
    List<Medecin> findMedecinsWithMinimumRendezVous(@Param("minRendezVous") Long minRendezVous);

    boolean existsByEmail(String email);

    boolean existsByTelephone(String telephone);

    List<Medecin> findAllByOrderByNomAsc();

    @Query("SELECT DISTINCT m FROM Medecin m JOIN m.specialites s WHERE s.nomspecialite IN :specialites")
    List<Medecin> findBySpecialiteNames(@Param("specialites") List<String> specialites);

    @Query("SELECT DISTINCT m FROM Medecin m JOIN m.specialites s WHERE s.id IN :specialiteIds")
    List<Medecin> findBySpecialiteIds(@Param("specialiteIds") List<Long> specialiteIds);
}