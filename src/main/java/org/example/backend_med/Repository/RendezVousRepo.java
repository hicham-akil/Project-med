package org.example.backend_med.Repository;

import org.example.backend_med.Models.RendezVous;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RendezVousRepo extends JpaRepository<RendezVous, Long> {

    List<RendezVous> findAllByOrderByHoraire_DateAscHoraire_HeureDebutAsc();

    List<RendezVous> findByPatientId(Long patientId);

    List<RendezVous> findAllByMedecinId(Long medecinId);

    List<RendezVous> findByStatus(String status);

    @Query("SELECT r FROM RendezVous r WHERE r.medecin.id = :medecinId AND r.horaire.date = :date")
    List<RendezVous> findByMedecinAndDate(@Param("medecinId") Long medecinId, @Param("date") LocalDate date);

    @Query("SELECT r FROM RendezVous r WHERE r.patient.id = :patientId AND r.status = :status")
    List<RendezVous> findByPatientAndStatus(@Param("patientId") Long patientId, @Param("status") String status);

    @Query("SELECT r FROM RendezVous r WHERE r.patient.id = :patientId " +
            "AND (r.horaire.date > :currentDate OR (r.horaire.date = :currentDate AND r.horaire.heureDebut > :currentTime)) " +
            "ORDER BY r.horaire.date ASC, r.horaire.heureDebut ASC")
    List<RendezVous> findUpcomingByPatientId(@Param("patientId") Long patientId,
                                             @Param("currentDate") LocalDate currentDate,
                                             @Param("currentTime") LocalTime currentTime);

    @Query("SELECT r FROM RendezVous r WHERE r.medecin.id = :medecinId " +
            "AND (r.horaire.date > :currentDate OR (r.horaire.date = :currentDate AND r.horaire.heureDebut > :currentTime)) " +
            "ORDER BY r.horaire.date ASC, r.horaire.heureDebut ASC")
    List<RendezVous> findUpcomingByMedecinId(@Param("medecinId") Long medecinId,
                                             @Param("currentDate") LocalDate currentDate,
                                             @Param("currentTime") LocalTime currentTime);

    @Query("SELECT r FROM RendezVous r WHERE r.patient.id = :patientId " +
            "AND (r.horaire.date < :currentDate OR (r.horaire.date = :currentDate AND r.horaire.heureDebut < :currentTime)) " +
            "ORDER BY r.horaire.date DESC, r.horaire.heureDebut DESC")
    List<RendezVous> findPastByPatientId(@Param("patientId") Long patientId,
                                         @Param("currentDate") LocalDate currentDate,
                                         @Param("currentTime") LocalTime currentTime);

    @Query("SELECT r FROM RendezVous r WHERE r.medecin.id = :medecinId " +
            "AND r.horaire.date = :date AND r.status = 'EN_ATTENTE' " +
            "ORDER BY r.queueNumber ASC")
    List<RendezVous> findWaitingByMedecinAndDate(@Param("medecinId") Long medecinId,
                                                 @Param("date") LocalDate date);

    @Query("SELECT r FROM RendezVous r WHERE r.medecin.id = :medecinId AND r.status = 'EN_COURS'")
    Optional<RendezVous> findInProgressByMedecin(@Param("medecinId") Long medecinId);

    @Query("SELECT COUNT(r) FROM RendezVous r WHERE r.medecin.id = :medecinId " +
            "AND r.horaire.date = :date AND r.status != 'ANNULE'")
    long countByMedecinAndDate(@Param("medecinId") Long medecinId,
                               @Param("date") LocalDate date);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM RendezVous r " +
            "WHERE r.patient.id = :patientId " +
            "AND r.medecin.id = :medecinId " +
            "AND r.status NOT IN ('ANNULE', 'TERMINE', 'COMPLETED')")
    boolean existsActiveRendezVous(@Param("patientId") Long patientId,
                                   @Param("medecinId") Long medecinId);
}