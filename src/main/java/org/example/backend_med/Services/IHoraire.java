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
    List<Horaire> getAvailableHorairesByMedecinId(Long medecinId);
    List<AvailableHoraireDTO> getAvailableHorairesWithSlots(Long medecinId);
    List<Horaire> getHorairesByJour(String joursSemaine);
    List<Horaire> getHorairesByStatus(String status);
    List<Horaire> getHorairesByMedecinAndJour(Long medecinId, String joursSemaine);
    Horaire updateHoraire(Long id, Horaire horaire);
    void deleteHoraire(Long id);

    List<AvailableHoraireDTO> getAvailableTimeForDoctorOnDate(Long medecinId, LocalDate date);
}
