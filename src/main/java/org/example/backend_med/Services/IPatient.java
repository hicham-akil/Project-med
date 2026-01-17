package org.example.backend_med.Services;

import org.example.backend_med.Models.Patient;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IPatient {

    // Create
    Patient createPatient(Patient patient);

    // Read - Single
    Optional<Patient> getPatientById(Long id);

    // Read - All
    List<Patient> getAllPatients();

    // Read - By Email
    Optional<Patient> getPatientByEmail(String email);

    // Read - By Phone
    Optional<Patient> getPatientByTelephone(String telephone);

    // Read - Search by Name
    List<Patient> searchPatientsByName(String name);



    // Update
    Patient updatePatient(Long id, Patient patient);

    // Delete
    void deletePatient(Long id);

    // Check existence
    boolean existsById(Long id);

    // Check by email
    boolean existsByEmail(String email);

    // Count
    long countPatients();
}