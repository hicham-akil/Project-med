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
    public ResponseEntity<?> getRendezVousById(@PathVariable Long id) {
        Optional<RendezVous> rendezVous = rendezVousService.getRendezVousById(id);
        if (rendezVous.isPresent()) {
            return ResponseEntity.ok(rendezVous.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Rendez-vous non trouvé avec l'ID: " + id);
    }

    // Get all rendez-vous
    @GetMapping
    public ResponseEntity<List<RendezVous>> getAllRendezVous() {
        List<RendezVous> rendezVousList = rendezVousService.getAllRendezVous();
        return ResponseEntity.ok(rendezVousList);
    }

    // Get rendez-vous by patient
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<RendezVous>> getRendezVousByPatient(@PathVariable Long patientId) {
        List<RendezVous> rendezVousList = rendezVousService.getRendezVousByPatientId(patientId);
        return ResponseEntity.ok(rendezVousList);
    }

    // Get rendez-vous by medecin
    @GetMapping("/medecin/{medecinId}")
    public ResponseEntity<List<RendezVous>> getRendezVousByMedecin(@PathVariable Long medecinId) {
        List<RendezVous> rendezVousList = rendezVousService.getRendezVousByMedecinId(medecinId);
        return ResponseEntity.ok(rendezVousList);
    }

    // Get rendez-vous by date
    @GetMapping("/date/{date}")
    public ResponseEntity<List<RendezVous>> getRendezVousByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<RendezVous> rendezVousList = rendezVousService.getRendezVousByDate(date);
        return ResponseEntity.ok(rendezVousList);
    }

    // Get rendez-vous by date range
    @GetMapping("/date-range")
    public ResponseEntity<List<RendezVous>> getRendezVousByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<RendezVous> rendezVousList = rendezVousService.getRendezVousByDateRange(startDate, endDate);
        return ResponseEntity.ok(rendezVousList);
    }

    // Get rendez-vous by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<RendezVous>> getRendezVousByStatus(@PathVariable String status) {
        List<RendezVous> rendezVousList = rendezVousService.getRendezVousByStatus(status);
        return ResponseEntity.ok(rendezVousList);
    }

    // Get rendez-vous by medecin and date
    @GetMapping("/medecin/{medecinId}/date/{date}")
    public ResponseEntity<List<RendezVous>> getRendezVousByMedecinAndDate(
            @PathVariable Long medecinId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<RendezVous> rendezVousList = rendezVousService.getRendezVousByMedecinAndDate(medecinId, date);
        return ResponseEntity.ok(rendezVousList);
    }

    // Get rendez-vous by patient and status
    @GetMapping("/patient/{patientId}/status/{status}")
    public ResponseEntity<List<RendezVous>> getRendezVousByPatientAndStatus(
            @PathVariable Long patientId,
            @PathVariable String status) {
        List<RendezVous> rendezVousList = rendezVousService.getRendezVousByPatientAndStatus(patientId, status);
        return ResponseEntity.ok(rendezVousList);
    }

    // Get upcoming rendez-vous by patient
    @GetMapping("/patient/{patientId}/upcoming")
    public ResponseEntity<List<RendezVous>> getUpcomingRendezVousByPatient(@PathVariable Long patientId) {
        List<RendezVous> rendezVousList = rendezVousService.getUpcomingRendezVousByPatientId(patientId);
        return ResponseEntity.ok(rendezVousList);
    }

    // Get upcoming rendez-vous by medecin
    @GetMapping("/medecin/{medecinId}/upcoming")
    public ResponseEntity<List<RendezVous>> getUpcomingRendezVousByMedecin(@PathVariable Long medecinId) {
        List<RendezVous> rendezVousList = rendezVousService.getUpcomingRendezVousByMedecinId(medecinId);
        return ResponseEntity.ok(rendezVousList);
    }

    // Get past rendez-vous by patient
    @GetMapping("/patient/{patientId}/past")
    public ResponseEntity<List<RendezVous>> getPastRendezVousByPatient(@PathVariable Long patientId) {
        List<RendezVous> rendezVousList = rendezVousService.getPastRendezVousByPatientId(patientId);
        return ResponseEntity.ok(rendezVousList);
    }

    // Update rendez-vous
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRendezVous(@PathVariable Long id, @RequestBody RendezVous rendezVous) {
        try {
            RendezVous updated = rendezVousService.updateRendezVous(id, rendezVous);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Update rendez-vous status
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateRendezVousStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            RendezVous updated = rendezVousService.updateRendezVousStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Reschedule rendez-vous
    @PatchMapping("/{id}/reschedule")
    public ResponseEntity<?> rescheduleRendezVous(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newDateTime) {
        try {
            RendezVous rescheduled = rendezVousService.rescheduleRendezVous(id, newDateTime);
            return ResponseEntity.ok(rescheduled);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Confirm rendez-vous
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<?> confirmRendezVous(@PathVariable Long id) {
        try {
            RendezVous confirmed = rendezVousService.confirmRendezVous(id);
            return ResponseEntity.ok(confirmed);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Cancel rendez-vous
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelRendezVous(@PathVariable Long id) {
        try {
            RendezVous cancelled = rendezVousService.cancelRendezVous(id);
            return ResponseEntity.ok(cancelled);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Delete rendez-vous
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRendezVous(@PathVariable Long id) {
        try {
            rendezVousService.deleteRendezVous(id);
            return ResponseEntity.ok("Rendez-vous supprimé avec succès");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Check if rendez-vous exists
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        boolean exists = rendezVousService.existsById(id);
        return ResponseEntity.ok(exists);
    }

    // Check if time slot is available
    @GetMapping("/availability/medecin/{medecinId}")
    public ResponseEntity<Boolean> checkTimeSlotAvailability(
            @PathVariable Long medecinId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {
        boolean available = rendezVousService.isTimeSlotAvailable(medecinId, dateTime);
        return ResponseEntity.ok(available);
    }

    // Count rendez-vous by medecin and date
    @GetMapping("/count/medecin/{medecinId}/date/{date}")
    public ResponseEntity<Long> countRendezVousByMedecinAndDate(
            @PathVariable Long medecinId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        long count = rendezVousService.countRendezVousByMedecinAndDate(medecinId, date);
        return ResponseEntity.ok(count);
    }
}