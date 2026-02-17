package org.example.backend_med.Controller;

import lombok.RequiredArgsConstructor;
import org.example.backend_med.Models.RendezVous;
import org.example.backend_med.Services.IRendezVous;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rendezvous")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RendezVousController {

    private final IRendezVous rendezVousService;

    // Create a new rendez-vous
    @PostMapping
    public ResponseEntity<?> createRendezVous(@RequestBody RendezVous rendezVous) {
        try {
            RendezVous created = rendezVousService.createRendezVous(rendezVous);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Get rendez-vous by ID
    @GetMapping("/{id}")
    public ResponseEntity<RendezVous> getRendezVousById(@PathVariable Long id) {
        return rendezVousService.getRendezVousById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // Get all rendez-vous
    @GetMapping
    public ResponseEntity<List<RendezVous>> getAllRendezVous() {
        return ResponseEntity.ok(rendezVousService.getAllRendezVous());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<RendezVous>> getRendezVousByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(rendezVousService.getRendezVousByPatientId(patientId));
    }

    @GetMapping("/medecin/{medecinId}")
    public ResponseEntity<List<RendezVous>> getRendezVousByMedecin(@PathVariable Long medecinId) {
        return ResponseEntity.ok(rendezVousService.getRendezVousByMedecinId(medecinId));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<RendezVous>> getRendezVousByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(rendezVousService.getRendezVousByDate(date));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRendezVous(@PathVariable Long id, @RequestBody RendezVous rendezVous) {
        return ResponseEntity.ok(rendezVousService.updateRendezVous(id, rendezVous));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRendezVous(@PathVariable Long id) {
        rendezVousService.deleteRendezVous(id);
        return ResponseEntity.ok("Rendez-vous supprimé avec succès");
    }
}
