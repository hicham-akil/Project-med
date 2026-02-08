package org.example.backend_med.Controller;

import lombok.RequiredArgsConstructor;
import org.example.backend_med.Models.Medecin;
import org.example.backend_med.Models.Patient;
import org.example.backend_med.Models.Utlisateur;
import org.example.backend_med.Security.JwtUtil;
import org.example.backend_med.Services.ImageUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AuthController {

    private final org.example.backend_med.Services.AuthService authService;
    private final JwtUtil jwtUtil;
    private final ImageUploadService imageUploadService;

    @PostMapping(value = "/register", consumes = "multipart/form-data")
    public ResponseEntity<?> register(
            @RequestPart("data") Map<String, Object> request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ){
        try {

            String imageUrl = null;
            if (image != null && !image.isEmpty()) {
                imageUrl = imageUploadService.uploadImage(image);
                System.out.println("Image uploaded successfully: " + imageUrl);
            } else {
                System.out.println("No image provided or image is empty");
            }

            Utlisateur user = authService.register(request, imageUrl);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            response.put("nom", user.getNom());
            response.put("prenom", user.getPrenom());
            response.put("role", user.getRole());
            response.put("profileImageUrl", imageUrl);

            if (user instanceof Patient patient) {
                response.put("telephone", patient.getTelephone());
                response.put("adresse", patient.getAdresse());
                response.put("dateNaissance", patient.getDateNaissance());
            } else if (user instanceof Medecin medecin) {
                response.put("telephone", medecin.getTelephone());
                response.put("adresse", medecin.getAdresse());
                response.put("specialites", medecin.getSpecialites());
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage() != null ? e.getMessage() : e.getClass().getName());
            errorResponse.put("details", e.toString());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}