package org.example.backend_med.Controller;

import lombok.RequiredArgsConstructor;
import org.example.backend_med.Models.Administrateur;
import org.example.backend_med.Services.IAdministrateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/administrateurs")
@CrossOrigin(origins = "*")
public class AdministrateurController {
    @Autowired
    private IAdministrateur administrateurService;

    // Create
    @PostMapping
    public ResponseEntity<?> createAdministrateur(@RequestBody Administrateur administrateur) {
        try {
            Administrateur created = administrateurService.createAdministrateur(administrateur);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getAdministrateurById(@PathVariable Long id) {
        Optional<Administrateur> administrateur = administrateurService.getAdministrateurById(id);
        if (administrateur.isPresent()) {
            return ResponseEntity.ok(administrateur.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Administrateur non trouvé avec l'ID: " + id);
    }

    // Get all
    @GetMapping
    public ResponseEntity<List<Administrateur>> getAllAdministrateurs() {
        return ResponseEntity.ok(administrateurService.getAllAdministrateurs());
    }

    // Get by email
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getAdministrateurByEmail(@PathVariable String email) {
        Optional<Administrateur> administrateur = administrateurService.getAdministrateurByEmail(email);
        if (administrateur.isPresent()) {
            return ResponseEntity.ok(administrateur.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Administrateur non trouvé avec l'email: " + email);
    }

    // Get by role
    @GetMapping("/role/{role}")
    public ResponseEntity<List<Administrateur>> getByRole(@PathVariable String role) {
        return ResponseEntity.ok(administrateurService.getAdministrateursByRole(role));
    }

    // Get by niveau
    @GetMapping("/niveau/{niveau}")
    public ResponseEntity<List<Administrateur>> getByNiveau(@PathVariable String niveau) {
        return ResponseEntity.ok(administrateurService.getAdministrateursByNiveau(niveau));
    }



    // Search by name
    @GetMapping("/search")
    public ResponseEntity<List<Administrateur>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(administrateurService.searchAdministrateursByName(name));
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAdministrateur(@PathVariable Long id, @RequestBody Administrateur administrateur) {
        try {
            Administrateur updated = administrateurService.updateAdministrateur(id, administrateur);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Update role
    @PatchMapping("/{id}/role")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestParam String role) {
        try {
            Administrateur updated = administrateurService.updateAdministrateurRole(id, role);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdministrateur(@PathVariable Long id) {
        try {
            administrateurService.deleteAdministrateur(id);
            return ResponseEntity.ok("Administrateur supprimé avec succès");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Check if exists by ID
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        return ResponseEntity.ok(administrateurService.existsById(id));
    }

    // Check if exists by email
    @GetMapping("/email/{email}/exists")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
        return ResponseEntity.ok(administrateurService.existsByEmail(email));
    }

    // Count all
    @GetMapping("/count")
    public ResponseEntity<Long> countAdministrateurs() {
        return ResponseEntity.ok(administrateurService.countAdministrateurs());
    }

    // Count by role
    @GetMapping("/count/role/{role}")
    public ResponseEntity<Long> countByRole(@PathVariable String role) {
        return ResponseEntity.ok(administrateurService.funcountbyrole(role));
    }
}