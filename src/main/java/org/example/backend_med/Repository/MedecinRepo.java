package org.example.backend_med.Repository;

import org.example.backend_med.Models.Medecin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedecinRepo extends JpaRepository<Medecin,Long> {
}
