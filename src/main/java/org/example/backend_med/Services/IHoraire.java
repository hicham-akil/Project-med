package org.example.backend_med.Services;

import org.example.backend_med.Dto.AvailableHoraireDTO;
import org.example.backend_med.Models.Horaire;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IHoraire {
    List<Horaire> createHoraire(List<Horaire> horaires);
    Optional<Horaire> getHoraireById(Long id);
    List<Horaire> getAllHoraires();
    List<Horaire> getHorairesByMedecinId(Long medecinId);
    List<Horaire> getHorairesByDate(LocalDate date);           // ✅ replaces getHorairesByJour
    List<Horaire> getHorairesByStatus(String status);
    List<Horaire> getHorairesByMedecinAndDate(Long medecinId, LocalDate date); // ✅ replaces getHorairesByMedecinAndJour
    List<Horaire> getAvailableHorairesByMedecinId(Long medecinId);
    List<AvailableHoraireDTO> getAvailableHorairesWithSlots(Long medecinId);
    Horaire updateHoraire(Long id, Horaire horaire);
    void deleteHoraire(Long id);

    List<AvailableHoraireDTO> getAvailableTimeForDoctorOnDate(Long medecinId, LocalDate date);
}