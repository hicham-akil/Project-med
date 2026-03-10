package org.example.backend_med.Controller;

import lombok.RequiredArgsConstructor;
import org.example.backend_med.Dto.CreateRendezVousRequest;
import org.example.backend_med.Dto.RendezVousResponseDto;
import org.example.backend_med.Dto.UpdateStatusRequest;
import org.example.backend_med.Models.*;
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
    public ResponseEntity<?> create(@RequestBody CreateRendezVousRequest request) {
        try {
            RendezVous created = rendezVousService.createRendezVous(request);
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
    public  ResponseEntity< List<RendezVousResponseDto>>  getRendezVousByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(rendezVousService.getRendezVousByPatientId(patientId));
    }

    @GetMapping("/medecin/{medecinId}")
    public ResponseEntity<List<RendezVousResponseDto>> getRendezVousByMedecin(@PathVariable Long medecinId) {
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
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request) {
        try {
            if (request.getStatus() == null || request.getStatus().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le statut est requis");
            }
            RendezVousResponseDto updated = rendezVousService.updateStatus(id, request.getStatus());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRendezVous(@PathVariable Long id) {
        rendezVousService.deleteRendezVous(id);
        return ResponseEntity.ok("Rendez-vous supprimé avec succès");
    }
}
