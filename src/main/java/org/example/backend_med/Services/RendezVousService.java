package org.example.backend_med.Services;

import org.example.backend_med.Models.RendezVous;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class RendezVousService implements  IRendezVous{
    @Override
    public RendezVous createRendezVous(RendezVous rendezVous) {
      return null;
    }

    @Override
    public Optional<RendezVous> getRendezVousById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<RendezVous> getAllRendezVous() {
        return List.of();
    }

    @Override
    public List<RendezVous> getRendezVousByPatientId(Long patientId) {
        return List.of();
    }

    @Override
    public List<RendezVous> getRendezVousByMedecinId(Long medecinId) {
        return List.of();
    }

    @Override
    public List<RendezVous> getRendezVousByDate(LocalDate date) {
        return List.of();
    }

    @Override
    public List<RendezVous> getRendezVousByDateRange(LocalDate startDate, LocalDate endDate) {
        return List.of();
    }

    @Override
    public List<RendezVous> getRendezVousByStatus(String status) {
        return List.of();
    }

    @Override
    public List<RendezVous> getRendezVousByMedecinAndDate(Long medecinId, LocalDate date) {
        return List.of();
    }

    @Override
    public List<RendezVous> getRendezVousByPatientAndStatus(Long patientId, String status) {
        return List.of();
    }

    @Override
    public List<RendezVous> getUpcomingRendezVousByPatientId(Long patientId) {
        return List.of();
    }

    @Override
    public List<RendezVous> getUpcomingRendezVousByMedecinId(Long medecinId) {
        return List.of();
    }

    @Override
    public List<RendezVous> getPastRendezVousByPatientId(Long patientId) {
        return List.of();
    }

    @Override
    public RendezVous updateRendezVous(Long id, RendezVous rendezVous) {
        return null;
    }

    @Override
    public RendezVous updateRendezVousStatus(Long id, String status) {
        return null;
    }

    @Override
    public RendezVous rescheduleRendezVous(Long id, LocalDateTime newDateTime) {
        return null;
    }

    @Override
    public RendezVous confirmRendezVous(Long id) {
        return null;
    }

    @Override
    public RendezVous cancelRendezVous(Long id) {
        return null;
    }

    @Override
    public void deleteRendezVous(Long id) {

    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public boolean isTimeSlotAvailable(Long medecinId, LocalDateTime dateTime) {
        return false;
    }

    @Override
    public boolean hasConflict(Long medecinId, LocalDateTime startTime, LocalDateTime endTime) {
        return false;
    }

    @Override
    public long countRendezVous() {
        return 0;
    }

    @Override
    public long countRendezVousByStatus(String status) {
        return 0;
    }

    @Override
    public long countRendezVousByMedecinAndDate(Long medecinId, LocalDate date) {
        return 0;
    }
}
