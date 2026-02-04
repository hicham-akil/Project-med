package org.example.backend_med.Services;

import org.example.backend_med.Models.Patient;
import org.example.backend_med.Models.Medecin;
import org.example.backend_med.Repository.PatientRepo;
import org.example.backend_med.Repository.MedecinRepo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final PatientRepo patientRepo;
    private final MedecinRepo medecinRepo;

    public UserService(PatientRepo patientRepo, MedecinRepo medecinRepo) {
        this.patientRepo = patientRepo;
        this.medecinRepo = medecinRepo;
    }

    // ---------------------------
    // Get user by ID, automatically detect role
    // ---------------------------
    public Optional<?> getUserById(Long id) {
        Optional<Patient> patientOpt = patientRepo.findById(id);
        if (patientOpt.isPresent()) {
            return patientOpt;
        }

        Optional<Medecin> medecinOpt = medecinRepo.findById(id);
        return medecinOpt;
    }

    // ---------------------------
    // Update user by ID
    // ---------------------------
    public Object updateUser(Long id, Object updatedUser) {
        Optional<Patient> patientOpt = patientRepo.findById(id);
        if (patientOpt.isPresent()) {
            Patient existing = patientOpt.get();
            Patient u = (Patient) updatedUser;
            existing.setNom(u.getNom());
            existing.setPrenom(u.getPrenom());
            existing.setEmail(u.getEmail());
            existing.setTelephone(u.getTelephone());
            existing.setAdresse(u.getAdresse());
            existing.setDateNaissance(u.getDateNaissance());
            return patientRepo.save(existing);
        }

        Optional<Medecin> medecinOpt = medecinRepo.findById(id);
        if (medecinOpt.isPresent()) {
            Medecin existing = medecinOpt.get();
            Medecin u = (Medecin) updatedUser;
            existing.setNom(u.getNom());
            existing.setPrenom(u.getPrenom());
            existing.setEmail(u.getEmail());
            existing.setTelephone(u.getTelephone());
            existing.setAdresse(u.getAdresse());
            existing.setSpecialites(u.getSpecialites());
            return medecinRepo.save(existing);
        }

        throw new RuntimeException("User not found with id: " + id);
    }

    // ---------------------------
    // Delete user by ID
    // ---------------------------
    public void deleteUser(Long id) {
        Optional<Patient> patientOpt = patientRepo.findById(id);
        if (patientOpt.isPresent()) {
            patientRepo.deleteById(id);
            return;
        }

        Optional<Medecin> medecinOpt = medecinRepo.findById(id);
        if (medecinOpt.isPresent()) {
            medecinRepo.deleteById(id);
            return;
        }

        throw new RuntimeException("User not found with id: " + id);
    }


}
