package org.example.backend_med.Repository;

import org.example.backend_med.Models.Horaire;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HoraireRepo extends JpaRepository<Horaire,Long> {
}
