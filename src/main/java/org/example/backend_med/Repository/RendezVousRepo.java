package org.example.backend_med.Repository;

import org.example.backend_med.Models.RendezVous;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RendezVousRepo extends JpaRepository<RendezVous, Long> {

    // Find by Patient
    List<RendezVous> findByPatientId(Long patientId);

    // Find by Medecin
    List<RendezVous> findByMedecinId(Long medecinId);

    // Find by Status
    List<RendezVous> findByStatus(String status);

    // Find by Date (exact date)
    @Query("SELECT r FROM RendezVous r WHERE CAST(r.dateHeure AS date) = :date")
    List<RendezVous> findByDate(@Param("date") LocalDate date);

    // Find by Date Range
    @Query("SELECT r FROM RendezVous r WHERE r.dateHeure BETWEEN :startDate AND :endDate")
    List<RendezVous> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Find by Medecin and Date
    @Query("SELECT r FROM RendezVous r WHERE r.medecin.id = :medecinId AND CAST(r.dateHeure AS date) = :date")
    List<RendezVous> findByMedecinAndDate(@Param("medecinId") Long medecinId, @Param("date") LocalDate date);

    // Find by Patient and Status
    @Query("SELECT r FROM RendezVous r WHERE r.patient.id = :patientId AND r.status = :status")
    List<RendezVous> findByPatientAndStatus(@Param("patientId") Long patientId, @Param("status") String status);

    // Find Upcoming by Patient
    @Query("SELECT r FROM RendezVous r WHERE r.patient.id = :patientId AND r.dateHeure >= :now ORDER BY r.dateHeure ASC")
    List<RendezVous> findUpcomingByPatientId(@Param("patientId") Long patientId, @Param("now") LocalDateTime now);

    // Find Upcoming by Medecin
    @Query("SELECT r FROM RendezVous r WHERE r.medecin.id = :medecinId AND r.dateHeure >= :now ORDER BY r.dateHeure ASC")
    List<RendezVous> findUpcomingByMedecinId(@Param("medecinId") Long medecinId, @Param("now") LocalDateTime now);

    // Find Past by Patient
    @Query("SELECT r FROM RendezVous r WHERE r.patient.id = :patientId AND r.dateHeure < :now ORDER BY r.dateHeure DESC")
    List<RendezVous> findPastByPatientId(@Param("patientId") Long patientId, @Param("now") LocalDateTime now);

    // Check if time slot is available
    @Query("SELECT COUNT(r) > 0 FROM RendezVous r WHERE r.medecin.id = :medecinId AND r.dateHeure = :dateTime AND r.status != 'annulé'")
    boolean existsByMedecinAndDateTime(@Param("medecinId") Long medecinId, @Param("dateTime") LocalDateTime dateTime);

    // Count by Medecin and Date
    @Query("SELECT COUNT(r) FROM RendezVous r WHERE r.medecin.id = :medecinId AND CAST(r.dateHeure AS date) = :date")
    long countByMedecinAndDate(@Param("medecinId") Long medecinId, @Param("date") LocalDate date);

    // Find all ordered by date
    List<RendezVous> findAllByOrderByDateHeureDesc();
}