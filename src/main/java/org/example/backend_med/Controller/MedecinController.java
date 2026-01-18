package org.example.backend_med.Controller;

import lombok.RequiredArgsConstructor;
import org.example.backend_med.Models.Medecin;
import org.example.backend_med.Services.IMedecin;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medecins")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MedecinController {

    private final IMedecin medecinService;

    // Create a new medecin
    @PostMapping
    public ResponseEntity<?> createMedecin(@RequestBody Medecin medecin) {
        try {
            Medecin createdMedecin = medecinService.createMedecin(medecin);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMedecin);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Get medecin by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getMedecinById(@PathVariable Long id) {
        Optional<Medecin> medecin = medecinService.getMedecinById(id);
        if (medecin.isPresent()) {
            return ResponseEntity.ok(medecin.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Médecin non trouvé avec l'ID: " + id);
    }

    // Get all medecins
    @GetMapping
    public ResponseEntity<List<Medecin>> getAllMedecins() {
        List<Medecin> medecins = medecinService.getAllMedecins();
        return ResponseEntity.ok(medecins);
    }

    // Get medecins by specialite
    @GetMapping("/specialite/{specialite}")
    public ResponseEntity<List<Medecin>> getMedecinsBySpecialite(@PathVariable String specialite) {
        List<Medecin> medecins = medecinService.getMedecinsBySpecialite(specialite);
        return ResponseEntity.ok(medecins);
    }

    // Get medecin by email
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getMedecinByEmail(@PathVariable String email) {
        Optional<Medecin> medecin = medecinService.getMedecinByEmail(email);
        if (medecin.isPresent()) {
            return ResponseEntity.ok(medecin.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Médecin non trouvé avec l'email: " + email);
    }

    // Get medecin by telephone
    @GetMapping("/telephone/{telephone}")
    public ResponseEntity<?> getMedecinByTelephone(@PathVariable String telephone) {
        Optional<Medecin> medecin = medecinService.getMedecinByTelephone(telephone);
        if (medecin.isPresent()) {
            return ResponseEntity.ok(medecin.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Médecin non trouvé avec le téléphone: " + telephone);
    }

    // Get available medecins
    @GetMapping("/disponibles")
    public ResponseEntity<List<Medecin>> getAvailableMedecins() {
        List<Medecin> medecins = medecinService.getAvailableMedecins();
        return ResponseEntity.ok(medecins);
    }

    // Get available medecins by day
    @GetMapping("/disponibles/jour/{jour}")
    public ResponseEntity<List<Medecin>> getAvailableMedecinsByDay(@PathVariable String jour) {
        List<Medecin> medecins = medecinService.getAvailableMedecinsByDay(jour);
        return ResponseEntity.ok(medecins);
    }

    // Get available medecins by day and specialite
    @GetMapping("/disponibles/jour/{jour}/specialite/{specialiteId}")
    public ResponseEntity<List<Medecin>> getAvailableMedecinsByDayAndSpecialite(
            @PathVariable String jour,
            @PathVariable Long specialiteId) {
        List<Medecin> medecins = medecinService.getAvailableMedecinsByDayAndSpecialite(jour, specialiteId);
        return ResponseEntity.ok(medecins);
    }

    // Get available medecins by day and time
    @GetMapping("/disponibles/jour/{jour}/heure/{heure}")
    public ResponseEntity<List<Medecin>> getAvailableMedecinsByDayAndTime(
            @PathVariable String jour,
            @PathVariable String heure) {
        List<Medecin> medecins = medecinService.getAvailableMedecinsByDayAndTime(jour, heure);
        return ResponseEntity.ok(medecins);
    }

    // Get available medecins by day, time and specialite
    @GetMapping("/disponibles/jour/{jour}/heure/{heure}/specialite/{specialiteId}")
    public ResponseEntity<List<Medecin>> getAvailableMedecinsByDayTimeAndSpecialite(
            @PathVariable String jour,
            @PathVariable String heure,
            @PathVariable Long specialiteId) {
        List<Medecin> medecins = medecinService.getAvailableMedecinsByDayTimeAndSpecialite(jour, heure, specialiteId);
        return ResponseEntity.ok(medecins);
    }

    // Get available medecins by specialite
    @GetMapping("/disponibles/specialite/{specialiteId}")
    public ResponseEntity<List<Medecin>> getAvailableMedecinsBySpecialite(@PathVariable Long specialiteId) {
        List<Medecin> medecins = medecinService.getAvailableMedecinsBySpecialite(specialiteId);
        return ResponseEntity.ok(medecins);
    }

    // Search medecins by name
    @GetMapping("/search")
    public ResponseEntity<List<Medecin>> searchMedecinsByName(@RequestParam String name) {
        List<Medecin> medecins = medecinService.searchMedecinsByName(name);
        return ResponseEntity.ok(medecins);
    }

    // Update medecin
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedecin(@PathVariable Long id, @RequestBody Medecin medecin) {
        try {
            Medecin updatedMedecin = medecinService.updateMedecin(id, medecin);
            return ResponseEntity.ok(updatedMedecin);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Update medecin availability
    @PatchMapping("/{id}/disponibilite")
    public ResponseEntity<?> updateMedecinAvailability(
            @PathVariable Long id,
            @RequestParam boolean isAvailable) {
        try {
            Medecin updatedMedecin = medecinService.updateMedecinAvailability(id, isAvailable);
            return ResponseEntity.ok(updatedMedecin);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Deactivate medecin
    @PatchMapping("/{id}/desactiver")
    public ResponseEntity<?> deactivateMedecin(@PathVariable Long id) {
        try {
            Medecin deactivatedMedecin = medecinService.deactivateMedecin(id);
            return ResponseEntity.ok(deactivatedMedecin);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Delete medecin
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedecin(@PathVariable Long id) {
        try {
            medecinService.deleteMedecin(id);
            return ResponseEntity.ok("Médecin supprimé avec succès");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Check if medecin exists by ID
    @GetMapping("/{id}/existe")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        boolean exists = medecinService.existsById(id);
        return ResponseEntity.ok(exists);
    }

    // Check if medecin exists by email
    @GetMapping("/email/{email}/existe")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
        boolean exists = medecinService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    // Count all medecins
    @GetMapping("/count")
    public ResponseEntity<Long> countMedecins() {
        long count = medecinService.countMedecins();
        return ResponseEntity.ok(count);
    }

    // Count medecins by specialite
    @GetMapping("/count/specialite/{specialite}")
    public ResponseEntity<Long> countMedecinsBySpecialite(@PathVariable String specialite) {
        long count = medecinService.countMedecinsBySpecialite(specialite);
        return ResponseEntity.ok(count);
    }
}