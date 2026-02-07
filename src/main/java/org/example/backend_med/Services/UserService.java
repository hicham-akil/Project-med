package org.example.backend_med.Services;

import org.example.backend_med.Models.Patient;
import org.example.backend_med.Models.Medecin;
import org.example.backend_med.Repository.PatientRepo;
import org.example.backend_med.Repository.MedecinRepo;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
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
    // Update user by ID (profile info only)
    // ---------------------------
    public Object updateUser(Long id, Map<String, Object> updatedUserData, String imageUrl) {
        Optional<Patient> patientOpt = patientRepo.findById(id);
        if (patientOpt.isPresent()) {
            Patient existing = patientOpt.get();

            if (updatedUserData.containsKey("nom")) {
                existing.setNom((String) updatedUserData.get("nom"));
            }
            if (updatedUserData.containsKey("prenom")) {
                existing.setPrenom((String) updatedUserData.get("prenom"));
            }
            if (updatedUserData.containsKey("email")) {
                existing.setEmail((String) updatedUserData.get("email"));
            }
            if (updatedUserData.containsKey("telephone")) {
                existing.setTelephone((String) updatedUserData.get("telephone"));
            }
            if (updatedUserData.containsKey("adresse")) {
                existing.setAdresse((String) updatedUserData.get("adresse"));
            }
            if (updatedUserData.containsKey("dateNaissance")) {
                Object dateObj = updatedUserData.get("dateNaissance");
                if (dateObj instanceof String) {
                    existing.setDateNaissance(new Date());
                } else if (dateObj instanceof Date) {
                    existing.setDateNaissance((Date) dateObj);
                }
            }

            // Update image if provided
            if (imageUrl != null) {
                existing.setProfileImageUrl(imageUrl);
            }

            return patientRepo.save(existing);
        }

        Optional<Medecin> medecinOpt = medecinRepo.findById(id);
        if (medecinOpt.isPresent()) {
            Medecin existing = medecinOpt.get();

            if (updatedUserData.containsKey("nom")) {
                existing.setNom((String) updatedUserData.get("nom"));
            }
            if (updatedUserData.containsKey("prenom")) {
                existing.setPrenom((String) updatedUserData.get("prenom"));
            }
            if (updatedUserData.containsKey("email")) {
                existing.setEmail((String) updatedUserData.get("email"));
            }
            if (updatedUserData.containsKey("telephone")) {
                existing.setTelephone((String) updatedUserData.get("telephone"));
            }
            if (updatedUserData.containsKey("adresse")) {
                existing.setAdresse((String) updatedUserData.get("adresse"));
            }

            // Update image if provided
            if (imageUrl != null) {
                existing.setProfileImageUrl(imageUrl);
            }

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