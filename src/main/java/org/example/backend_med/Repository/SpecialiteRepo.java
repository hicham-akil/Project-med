package org.example.backend_med.Repository;

import org.example.backend_med.Models.Specialite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialiteRepo extends JpaRepository<Specialite,Long> {
}
