package org.example.backend_med.Services;

import jakarta.persistence.EntityNotFoundException;
import org.example.backend_med.Dto.UpdateUserRequest;
import org.example.backend_med.Models.Patient;
import org.example.backend_med.Models.Medecin;
import org.example.backend_med.Models.Utlisateur;
import org.example.backend_med.Repository.PatientRepo;
import org.example.backend_med.Repository.MedecinRepo;
import org.example.backend_med.Repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
@Service
public class UserService {

    private final UtilisateurRepository utilisateurRepo;

    public UserService(UtilisateurRepository utilisateurRepo) {
        this.utilisateurRepo = utilisateurRepo;
    }

    public Utlisateur getUserById(Long id) {
        return utilisateurRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable : " + id));
    }

    public Utlisateur updateUser(Long id, UpdateUserRequest req, String imageUrl) {
        Utlisateur user = getUserById(id);

        if (req.nom() != null && !req.nom().isBlank())       user.setNom(req.nom());
        if (req.prenom() != null && !req.prenom().isBlank()) user.setPrenom(req.prenom());
        if (req.email() != null && !req.email().isBlank())   user.setEmail(req.email());
        if (req.telephone() != null)                         user.setTelephone(req.telephone());
        if (req.adresse() != null)                           user.setAdresse(req.adresse());

        if (req.dateNaissance() != null && !req.dateNaissance().isBlank()) {
            String datePart = req.dateNaissance().contains("T")
                    ? req.dateNaissance().substring(0, 10)
                    : req.dateNaissance();
            user.setDateNaissance(
                    Date.from(
                            LocalDate.parse(datePart)
                                    .atStartOfDay(ZoneId.systemDefault())
                                    .toInstant()
                    )
            );
        }

        if (imageUrl != null) user.setProfileImageUrl(imageUrl);

        return utilisateurRepo.save(user);
    }
    public void deleteUser(Long id) {
        if (!utilisateurRepo.existsById(id)) {
            throw new EntityNotFoundException("Utilisateur introuvable : " + id);
        }
        utilisateurRepo.deleteById(id);
    }
}