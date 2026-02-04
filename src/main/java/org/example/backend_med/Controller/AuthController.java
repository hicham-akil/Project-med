package org.example.backend_med.Controller;

import lombok.RequiredArgsConstructor;
import org.example.backend_med.Models.Medecin;
import org.example.backend_med.Models.Patient;
import org.example.backend_med.Models.Utlisateur;
import org.example.backend_med.Security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AuthController {

    private final org.example.backend_med.Services.AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> request) {
        try {
            Utlisateur user = authService.register(request);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            response.put("nom", user.getNom());
            response.put("prenom", user.getPrenom());
            response.put("role", user.getRole());

            // Add specific fields based on role
            if (user instanceof Patient) {
                Patient patient = (Patient) user;
                if (patient.getTelephone() != null) {
                    response.put("telephone", patient.getTelephone());
                }
                if (patient.getAdresse() != null) {
                    response.put("adresse", patient.getAdresse());
                }
                if (patient.getDateNaissance() != null) {
                    response.put("dateNaissance", patient.getDateNaissance());
                }
            } else if (user instanceof Medecin) {
                Medecin medecin = (Medecin) user;
                if (medecin.getTelephone() != null) {
                    response.put("telephone", medecin.getTelephone());
                }
                if (medecin.getAdresse() != null) {
                    response.put("adresse", medecin.getAdresse());
                }
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace to console
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage() != null ? e.getMessage() : e.getClass().getName());
            errorResponse.put("details", e.toString());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            Utlisateur user = authService.login(
                    request.get("email"),
                    request.get("password")
            );

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            response.put("nom", user.getNom());
            response.put("prenom", user.getPrenom());
            response.put("role", user.getRole());

            // Add specific fields based on role
            if (user instanceof Patient) {
                Patient patient = (Patient) user;
                if (patient.getTelephone() != null) {
                    response.put("telephone", patient.getTelephone());
                }
                if (patient.getAdresse() != null) {
                    response.put("adresse", patient.getAdresse());
                }
                if (patient.getDateNaissance() != null) {
                    response.put("dateNaissance", patient.getDateNaissance());
                }
            } else if (user instanceof Medecin) {
                Medecin medecin = (Medecin) user;
                if (medecin.getTelephone() != null) {
                    response.put("telephone", medecin.getTelephone());
                }
                if (medecin.getAdresse() != null) {
                    response.put("adresse", medecin.getAdresse());
                }
            }
            String token =jwtUtil.GenerateJwtToken(user.getNom(), user.getRole(),user.getId());
            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "role", user.getRole(),
                    "id",user.getId(),
                    "token", token
            ));
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace to console
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage() != null ? e.getMessage() : e.getClass().getName());
            errorResponse.put("details", e.toString());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}