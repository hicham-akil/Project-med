package org.example.backend_med.Controller;

import lombok.RequiredArgsConstructor;
import org.example.backend_med.Models.Specialite;
import org.example.backend_med.Services.ISpecialite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/specialites")
@CrossOrigin(origins = "*")
public class SpecialiteController {

    @Autowired
    private ISpecialite specialiteService;

    // Create
    @PostMapping
    public ResponseEntity<?> createSpecialite(@RequestBody Specialite specialite) {
        try {
            Specialite created = specialiteService.createSpecialite(specialite);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getSpecialiteById(@PathVariable Long id) {
        Optional<Specialite> specialite = specialiteService.getSpecialiteById(id);
        if (specialite.isPresent()) {
            return ResponseEntity.ok(specialite.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Spécialité non trouvée avec l'ID: " + id);
    }

    // Get all
    @GetMapping
    public ResponseEntity<List<Specialite>> getAllSpecialites() {
        return ResponseEntity.ok(specialiteService.getAllSpecialites());
    }

    // Get by name
    @GetMapping("/nom/{nomspecialite}")
    public ResponseEntity<?> getSpecialiteByName(@PathVariable String nomspecialite) {
        Optional<Specialite> specialite = specialiteService.getSpecialiteByName(nomspecialite);
        if (specialite.isPresent()) {
            return ResponseEntity.ok(specialite.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Spécialité non trouvée: " + nomspecialite);
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSpecialite(@PathVariable Long id, @RequestBody Specialite specialite) {
        try {
            Specialite updated = specialiteService.updateSpecialite(id, specialite);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSpecialite(@PathVariable Long id) {
        try {
            specialiteService.deleteSpecialite(id);
            return ResponseEntity.ok("Spécialité supprimée avec succès");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}