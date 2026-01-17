package org.example.backend_med.Services;

import org.example.backend_med.Models.Horaire;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class HoraireService implements IHoraire {
    @Override
    public Horaire createHoraire(Horaire horaire) {
        return null;
    }

    @Override
    public Optional<Horaire> getHoraireById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Horaire> getAllHoraires() {
        return List.of();
    }

    @Override
    public List<Horaire> getHorairesByDate(LocalDate date) {
        return List.of();
    }

    @Override
    public List<Horaire> getHorairesByDateRange(LocalDate startDate, LocalDate endDate) {
        return List.of();
    }

    @Override
    public List<Horaire> getHorairesByMedecinId(Long medecinId) {
        return List.of();
    }

    @Override
    public List<Horaire> getHorairesByDayOfWeek(String dayOfWeek) {
        return List.of();
    }

    @Override
    public Horaire updateHoraire(Long id, Horaire horaire) {
        return null;
    }

    @Override
    public void deleteHoraire(Long id) {

    }

    @Override
    public void deleteHorairesByDate(LocalDate date) {

    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public boolean isTimeSlotAvailable(LocalDate date, LocalTime startTime, LocalTime endTime) {
        return false;
    }

    @Override
    public long countHoraires() {
        return 0;
    }
}
