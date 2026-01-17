package org.example.backend_med.Services;

import org.example.backend_med.Models.Patient;
import org.example.backend_med.Repository.PatientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PatientService implements IPatient {

    @Autowired
    private PatientRepo patientRepo;

    @Override
    public Patient createPatient(Patient patient) {
        patient.setDateCreation(new Date());
        patient.setDateModification(new Date());
        patient.setRole("PATIENT");
        return patientRepo.save(patient);
    }

    @Override
    public Optional<Patient> getPatientById(Long id) {
        return patientRepo.findById(id);
    }

    @Override
    public List<Patient> getAllPatients() {
        return patientRepo.findAll();
    }

    @Override
    public Optional<Patient> getPatientByEmail(String email) {
        return patientRepo.findByEmail(email);
    }

    @Override
    public Optional<Patient> getPatientByTelephone(String telephone) {
        return patientRepo.findByTelephone(telephone);
    }

    @Override
    public List<Patient> searchPatientsByName(String name) {
        return patientRepo.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(name, name);
    }



    @Override
    public Patient updatePatient(Long id, Patient patient) {
        return patientRepo.findById(id)
                .map(existingPatient -> {
                    existingPatient.setNom(patient.getNom());
                    existingPatient.setPrenom(patient.getPrenom());
                    existingPatient.setEmail(patient.getEmail());
                    existingPatient.setTelephone(patient.getTelephone());
                    existingPatient.setDateNaissance(patient.getDateNaissance());
                    existingPatient.setAdresse(patient.getAdresse());
                    if (patient.getPassword() != null && !patient.getPassword().isEmpty()) {
                        existingPatient.setPassword(patient.getPassword());
                    }
                    existingPatient.setDateModification(new Date());
                    return patientRepo.save(existingPatient);
                })
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
    }

    @Override
    public void deletePatient(Long id) {
        if (!patientRepo.existsById(id)) {
            throw new RuntimeException("Patient not found with id: " + id);
        }
        patientRepo.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return patientRepo.existsById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return patientRepo.existsByEmail(email);
    }

    @Override
    public long countPatients() {
        return patientRepo.count();
    }
}