package org.example.backend_med.Services;

import org.example.backend_med.Models.Horaire;

import java.util.List;
import java.util.Optional;

public interface IHoraire {

    // Create
    List<Horaire>  createHoraire(List<Horaire> horaires);

    // Read
    Optional<Horaire> getHoraireById(Long id);
    List<Horaire> getAllHoraires();
    List<Horaire> getHorairesByMedecinId(Long medecinId);
    List<Horaire> getHorairesByJour(String joursSemaine);
    List<Horaire> getHorairesByStatus(String status);
    List<Horaire> getHorairesByMedecinAndJour(Long medecinId, String joursSemaine);

    // Update
    Horaire updateHoraire(Long id, Horaire horaire);

    // Delete
    void deleteHoraire(Long id);
}