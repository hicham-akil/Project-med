package org.example.backend_med.Services;

import lombok.RequiredArgsConstructor;
import org.example.backend_med.Models.Medecin;
import org.example.backend_med.Models.Patient;
import org.example.backend_med.Models.Specialite;
import org.example.backend_med.Models.Utlisateur;
import org.example.backend_med.Repository.SpecialiteRepo;
import org.example.backend_med.Repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final SpecialiteRepo specialiteRepo;
    public Utlisateur register(Map<String, Object> request, String imageUrl) {
        String role = (String) request.get("role");
        String email = (String) request.get("email");

        if (utilisateurRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        Utlisateur user;

        if ("PATIENT".equalsIgnoreCase(role)) {
            Patient patient = new Patient();
            patient.setTelephone((String) request.get("telephone"));
            patient.setAdresse((String) request.get("adresse"));

            if (request.get("dateNaissance") != null) {
                try {
                    String dateStr = (String) request.get("dateNaissance");
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    patient.setDateNaissance(formatter.parse(dateStr));
                } catch (Exception e) {
                    throw new RuntimeException("Invalid date format. Use yyyy-MM-dd");
                }
            }
            user = patient;

        } else if ("MEDECIN".equalsIgnoreCase(role)) {
            Medecin medecin = new Medecin();
            medecin.setTelephone((String) request.get("telephone"));
            medecin.setAdresse((String) request.get("adresse"));
            if (request.get("specialites") != null) {
                List<Long> specialiteIds = (List<Long>) request.get("specialites");
                List<Specialite> specialites = specialiteRepo.findAllById(specialiteIds);

                if (specialites.isEmpty()) {
                    throw new RuntimeException("No valid specialities found");
                }

                medecin.setSpecialites(specialites);
            }

            user = medecin;

        } else {
            throw new RuntimeException("Invalid role. Must be PATIENT or MEDECIN");
        }

        user.setNom((String) request.get("nom"));
        user.setPrenom((String) request.get("prenom"));
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode((String) request.get("password")));
        user.setRole(role.toUpperCase());
        user.setDateCreation(new Date());

        if (imageUrl != null && !imageUrl.isEmpty()) {
            user.setProfileImageUrl(imageUrl);
        }

        return utilisateurRepository.save(user);
    }

    public Utlisateur login(String email, String password) {
        Utlisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }
}