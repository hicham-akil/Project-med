package org.example.backend_med.Repository;

import org.example.backend_med.Models.Administrateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepo extends JpaRepository<Administrateur,Long> {
}
