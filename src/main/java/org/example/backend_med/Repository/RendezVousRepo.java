package org.example.backend_med.Repository;

import org.example.backend_med.Dto.RendezVousResponseDto;
import org.example.backend_med.Models.RendezVous;
import org.example.backend_med.Models.RendezVousStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RendezVousRepo extends JpaRepository<RendezVous, Long> {

    // Find all ordered by date (newest first)
    List<RendezVous> findAllByOrderByDateHeureDebutDesc();

    // Find by patient
    List<RendezVous> findByPatientId(Long patientId);

    // Find by medecin
    List<RendezVous> findAllByMedecinId(Long medecinId);

    // Find by status
    List<RendezVous> findByStatus(String status);

    // Find by specific date
    @Query("SELECT r FROM RendezVous r WHERE CAST(r.dateHeureDebut AS date) = :date")
    List<RendezVous> findByDate(@Param("date") LocalDate date);

    // Find by date range
    @Query("SELECT r FROM RendezVous r WHERE r.dateHeureDebut >= :startDateTime AND r.dateHeureDebut <= :endDateTime")
    List<RendezVous> findByDateRange(@Param("startDateTime") LocalDateTime startDateTime,
                                     @Param("endDateTime") LocalDateTime endDateTime);

    // Find by medecin and date
    @Query("SELECT r FROM RendezVous r WHERE r.medecin.id = :medecinId AND CAST(r.dateHeureDebut AS date) = :date")
    List<RendezVous> findByMedecinAndDate(@Param("medecinId") Long medecinId, @Param("date") LocalDate date);

    // Find by patient and status
    @Query("SELECT r FROM RendezVous r WHERE r.patient.id = :patientId AND r.status = :status")
    List<RendezVous> findByPatientAndStatus(@Param("patientId") Long patientId, @Param("status") String status);

    // Find upcoming appointments for patient
    @Query("SELECT r FROM RendezVous r WHERE r.patient.id = :patientId AND r.dateHeureDebut > :currentDateTime ORDER BY r.dateHeureDebut ASC")
    List<RendezVous> findUpcomingByPatientId(@Param("patientId") Long patientId, @Param("currentDateTime") LocalDateTime currentDateTime);

    // Find upcoming appointments for medecin
    @Query("SELECT r FROM RendezVous r WHERE r.medecin.id = :medecinId AND r.dateHeureDebut > :currentDateTime ORDER BY r.dateHeureDebut ASC")
    List<RendezVous> findUpcomingByMedecinId(@Param("medecinId") Long medecinId, @Param("currentDateTime") LocalDateTime currentDateTime);

    // Find past appointments for patient
    @Query("SELECT r FROM RendezVous r WHERE r.patient.id = :patientId AND r.dateHeureDebut < :currentDateTime ORDER BY r.dateHeureDebut DESC")
    List<RendezVous> findPastByPatientId(@Param("patientId") Long patientId, @Param("currentDateTime") LocalDateTime currentDateTime);

    // Check if exists by medecin and datetime (DEPRECATED - use countOverlappingAppointments instead)
    @Deprecated
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM RendezVous r WHERE r.medecin.id = :medecinId AND r.dateHeureDebut = :dateTime")
    boolean existsByMedecinAndDateTime(@Param("medecinId") Long medecinId, @Param("dateTime") LocalDateTime dateTime);

    // Count overlapping appointments
    @Query("SELECT COUNT(r) FROM RendezVous r WHERE r.medecin.id = :medecinId " +
            "AND (r.dateHeureDebut < :endTime AND r.dateHeureFin > :startTime) " +
            "AND (:horaireId IS NULL OR r.horaire.idHoraire = :horaireId)")
    long countOverlappingAppointments(
            @Param("medecinId") Long medecinId,
            @Param("horaireId") Long horaireId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );




    @Query("SELECT r FROM RendezVous r WHERE r.medecin.id = :medecinId " +
            "AND DATE(r.dateHeureDebut) = :date AND r.status = 'EN_ATTENTE' " +
            "ORDER BY r.queueNumber ASC")
    List<RendezVous> findWaitingByMedecinAndDate(
            @Param("medecinId") Long medecinId,
            @Param("date") LocalDate date
    );

    // Find currently in-progress appointment for a doctor
    @Query("SELECT r FROM RendezVous r WHERE r.medecin.id = :medecinId " +
            "AND r.status = 'EN_COURS'")
    Optional<RendezVous> findInProgressByMedecin(@Param("medecinId") Long medecinId);

    // Count appointments for a doctor on a specific date (for queue numbering)
    @Query("SELECT COUNT(r) FROM RendezVous r WHERE r.medecin.id = :medecinId " +
            "AND DATE(r.dateHeureDebut) = :date AND r.status != 'ANNULE'")
    long countByMedecinAndDate(
            @Param("medecinId") Long medecinId,
            @Param("date") LocalDate date
    );


    List<RendezVous> findByMedecinId(Long medecinId);
}