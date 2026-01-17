package org.example.backend_med.Services;

import org.example.backend_med.Models.Horaire;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface IHoraire {

    // Create
    Horaire createHoraire(Horaire horaire);

    // Read - Single
    Optional<Horaire> getHoraireById(Long id);

    // Read - All
    List<Horaire> getAllHoraires();

    // Read - By Date
    List<Horaire> getHorairesByDate(LocalDate date);

    // Read - By Date Range
    List<Horaire> getHorairesByDateRange(LocalDate startDate, LocalDate endDate);

    // Read - By Doctor/Staff (if applicable)
    List<Horaire> getHorairesByMedecinId(Long medecinId);

    // Read - By Day of Week
    List<Horaire> getHorairesByDayOfWeek(String dayOfWeek);

    // Update
    Horaire updateHoraire(Long id, Horaire horaire);

    // Delete
    void deleteHoraire(Long id);

    // Delete - By Date
    void deleteHorairesByDate(LocalDate date);

    // Check existence
    boolean existsById(Long id);

    // Check availability
    boolean isTimeSlotAvailable(LocalDate date, LocalTime startTime, LocalTime endTime);

    // Count
    long countHoraires();
}