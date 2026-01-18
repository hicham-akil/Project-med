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

    // Find all medecins by specialite
    List<Medecin> findBySpecialite(String specialite);

    // Find medecins by telephone
    Optional<Medecin> findByTelephone(String telephone);

    // Find medecins by adresse (partial match)
    List<Medecin> findByAdresseContainingIgnoreCase(String adresse);

    // Find medecins by specialite entity
    @Query("SELECT m FROM Medecin m JOIN m.specialites s WHERE s.id = :specialiteId")
    List<Medecin> findBySpecialiteId(@Param("specialiteId") Long specialiteId);

    // Find medecins by specialite name
    @Query("SELECT m FROM Medecin m JOIN m.specialites s WHERE s.nomspecialite = :nomSpecialite")
    List<Medecin> findBySpecialiteName(@Param("nomSpecialite") String nomSpecialite);

    // Find medecins with available horaires
    @Query("SELECT DISTINCT m FROM Medecin m JOIN m.horairesDisponibles h WHERE h.status = 'disponible'")
    List<Medecin> findMedecinsWithAvailableHoraires();

    // Find medecins by nom or prenom (inherited from Utilisateur)
    List<Medecin> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);

    // Count medecins by specialite
    @Query("SELECT COUNT(m) FROM Medecin m JOIN m.specialites s WHERE s.id = :specialiteId")
    Long countBySpecialiteId(@Param("specialiteId") Long specialiteId);

    // Find medecins with rendez-vous count
    @Query("SELECT m FROM Medecin m LEFT JOIN m.rendezVous r GROUP BY m HAVING COUNT(r) > :minRendezVous")
    List<Medecin> findMedecinsWithMinimumRendezVous(@Param("minRendezVous") Long minRendezVous);

    // Check if medecin exists by email
    boolean existsByEmail(String email);

    // Check if medecin exists by telephone
    boolean existsByTelephone(String telephone);

    // Find all medecins ordered by nom
    List<Medecin> findAllByOrderByNomAsc();

    // Find medecins by multiple specialites
    @Query("SELECT DISTINCT m FROM Medecin m JOIN m.specialites s WHERE s.nomspecialite IN :specialites")
    List<Medecin> findBySpecialiteNames(@Param("specialites") List<String> specialites);
}