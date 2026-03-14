package org.example.backend_med.Controller;

import lombok.RequiredArgsConstructor;
import org.example.backend_med.Dto.CreateRendezVousRequest;
import org.example.backend_med.Dto.RendezVousResponseDto;
import org.example.backend_med.Dto.UpdateStatusRequest;
import org.example.backend_med.Models.RendezVous;
import org.example.backend_med.Services.IRendezVous;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rendezvous")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RendezVousController {

    private final IRendezVous rendezVousService;

    // ── BOOK appointment (patient books, gets queue number) ──
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateRendezVousRequest request) {
        try {
            RendezVous created = rendezVousService.createRendezVous(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ── NEXT PATIENT — doctor clicks "Next" ──
    @PostMapping("/medecin/{medecinId}/next")
    public ResponseEntity<?> callNext(@PathVariable Long medecinId) {
        try {
            RendezVousResponseDto next = rendezVousService.callNextPatient(medecinId);
            return ResponseEntity.ok(next);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ── TODAY'S QUEUE for a doctor ──
    @GetMapping("/medecin/{medecinId}/queue")
    public ResponseEntity<List<RendezVousResponseDto>> getTodayQueue(
            @PathVariable Long medecinId) {
        return ResponseEntity.ok(rendezVousService.getTodayQueueByMedecin(medecinId));
    }

    // ── GET by ID ──
    @GetMapping("/{id}")
    public ResponseEntity<RendezVous> getById(@PathVariable Long id) {
        return rendezVousService.getRendezVousById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── GET all ──
    @GetMapping
    public ResponseEntity<List<RendezVous>> getAll() {
        return ResponseEntity.ok(rendezVousService.getAllRendezVous());
    }

    // ── GET by patient ──
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<RendezVousResponseDto>> getByPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(rendezVousService.getRendezVousByPatientId(patientId));
    }

    // ── GET by medecin ──
    @GetMapping("/medecin/{medecinId}")
    public ResponseEntity<List<RendezVousResponseDto>> getByMedecin(
            @PathVariable Long medecinId) {
        return ResponseEntity.ok(rendezVousService.getRendezVousByMedecinId(medecinId));
    }

    // ── GET by date ──
    @GetMapping("/date/{date}")
    public ResponseEntity<List<RendezVous>> getByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(rendezVousService.getRendezVousByDate(date));
    }

    // ── UPDATE STATUS (manual override) ──
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request) {
        try {
            if (request.getStatus() == null || request.getStatus().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le statut est requis");
            }
            return ResponseEntity.ok(rendezVousService.updateStatus(id, request.getStatus()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ── CANCEL ──
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(rendezVousService.cancelRendezVous(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ── DELETE ──
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            rendezVousService.deleteRendezVous(id);
            return ResponseEntity.ok("Rendez-vous supprimé avec succès");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}