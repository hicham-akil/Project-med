package org.example.backend_med.Services;

import org.example.backend_med.Models.Specialite;

import java.util.List;
import java.util.Optional;

public interface ISpecialite {

    // Create
    Specialite createSpecialite(Specialite specialite);

    // Read
    Optional<Specialite> getSpecialiteById(Long id);
    List<Specialite> getAllSpecialites();
    Optional<Specialite> getSpecialiteByName(String nomspecialite);

    // Update
    Specialite updateSpecialite(Long id, Specialite specialite);

    // Delete
    void deleteSpecialite(Long id);
}

