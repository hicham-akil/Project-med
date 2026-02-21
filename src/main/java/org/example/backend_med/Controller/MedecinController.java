package org.example.backend_med.Controller;

import lombok.RequiredArgsConstructor;
import org.example.backend_med.Models.Medecin;
import org.example.backend_med.Services.IMedecin;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medecins")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MedecinController {

    private final IMedecin medecinService;

    @PostMapping
    public ResponseEntity<?> createMedecin(@RequestBody Medecin medecin) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(medecinService.createMedecin(medecin));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMedecinById(@PathVariable Long id) {
        Optional<Medecin> medecin = medecinService.getMedecinById(id);
        return medecin.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping
    public ResponseEntity<List<Medecin>> getAllMedecins() {
        return ResponseEntity.ok(medecinService.getAllMedecins());
    }

    @GetMapping("/specialite/{specialiteId}")
    public ResponseEntity<List<Medecin>> getMedecinsBySpecialite(@PathVariable Long specialiteId) {
        return ResponseEntity.ok(medecinService.getMedecinsBySpecialite(specialiteId));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getMedecinByEmail(@PathVariable String email) {
        Optional<Medecin> medecin = medecinService.getMedecinByEmail(email);
        return medecin.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/telephone/{telephone}")
    public ResponseEntity<?> getMedecinByTelephone(@PathVariable String telephone) {
        Optional<Medecin> medecin = medecinService.getMedecinByTelephone(telephone);
        return medecin.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<Medecin>> getAvailableMedecins() {
        return ResponseEntity.ok(medecinService.getAvailableMedecins());
    }

    // ✅ Replaced /jour/{jour} with ?date=2026-03-03
    @GetMapping("/disponibles/date")
    public ResponseEntity<List<Medecin>> getAvailableMedecinsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(medecinService.getAvailableMedecinsByDate(date));
    }

    // ✅ Replaced /jour/{jour}/specialite/{id} with ?date=...&specialiteId=...
    @GetMapping("/disponibles/date/specialite/{specialiteId}")
    public ResponseEntity<List<Medecin>> getAvailableMedecinsByDateAndSpecialite(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable Long specialiteId) {
        return ResponseEntity.ok(medecinService.getAvailableMedecinsByDateAndSpecialite(date, specialiteId));
    }

    // ✅ Replaced /jour/{jour}/heure/{heure} with ?date=...&heure=...
    @GetMapping("/disponibles/date/heure")
    public ResponseEntity<List<Medecin>> getAvailableMedecinsByDateAndTime(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String heure) {
        return ResponseEntity.ok(medecinService.getAvailableMedecinsByDateAndTime(date, heure));
    }

    // ✅ Replaced /jour/{jour}/heure/{heure}/specialite/{id} with ?date=...&heure=...
    @GetMapping("/disponibles/date/heure/specialite/{specialiteId}")
    public ResponseEntity<List<Medecin>> getAvailableMedecinsByDateTimeAndSpecialite(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String heure,
            @PathVariable Long specialiteId) {
        return ResponseEntity.ok(medecinService.getAvailableMedecinsByDateTimeAndSpecialite(date, heure, specialiteId));
    }

    @GetMapping("/disponibles/specialite/{specialiteId}")
    public ResponseEntity<List<Medecin>> getAvailableMedecinsBySpecialite(@PathVariable Long specialiteId) {
        return ResponseEntity.ok(medecinService.getAvailableMedecinsBySpecialite(specialiteId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Medecin>> searchMedecinsByName(@RequestParam String name) {
        return ResponseEntity.ok(medecinService.searchMedecinsByName(name));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedecin(@PathVariable Long id, @RequestBody Medecin medecin) {
        try {
            return ResponseEntity.ok(medecinService.updateMedecin(id, medecin));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/disponibilite")
    public ResponseEntity<?> updateMedecinAvailability(
            @PathVariable Long id,
            @RequestParam boolean isAvailable) {
        try {
            return ResponseEntity.ok(medecinService.updateMedecinAvailability(id, isAvailable));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/desactiver")
    public ResponseEntity<?> deactivateMedecin(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(medecinService.deactivateMedecin(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedecin(@PathVariable Long id) {
        try {
            medecinService.deleteMedecin(id);
            return ResponseEntity.ok("Médecin supprimé avec succès");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/existe")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        return ResponseEntity.ok(medecinService.existsById(id));
    }

    @GetMapping("/email/{email}/existe")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
        return ResponseEntity.ok(medecinService.existsByEmail(email));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countMedecins() {
        return ResponseEntity.ok(medecinService.countMedecins());
    }

    @GetMapping("/count/specialite/{specialite}")
    public ResponseEntity<Long> countMedecinsBySpecialite(@PathVariable String specialite) {
        return ResponseEntity.ok(medecinService.countMedecinsBySpecialite(specialite));
    }
}