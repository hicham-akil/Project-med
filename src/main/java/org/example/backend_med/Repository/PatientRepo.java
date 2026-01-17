package org.example.backend_med.Repository;

import org.example.backend_med.Models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepo extends JpaRepository<Patient,Long> {
}
