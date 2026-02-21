package org.example.backend_med.Repository;

import org.example.backend_med.Models.Horaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface HoraireRepo extends JpaRepository<Horaire, Long> {

    List<Horaire> findAllByMedecinId(Long medecinId);

    List<Horaire> findByDate(LocalDate date);

    List<Horaire> findByStatus(String status);

    List<Horaire> findByMedecinIdAndDate(Long medecinId, LocalDate date);

    @Query("SELECT h FROM Horaire h WHERE h.medecin.id = :medecinId AND h.status = 'ACTIVE'")
    List<Horaire> findAvailableHorairesByMedecinId(@Param("medecinId") Long medecinId);

}