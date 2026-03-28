package org.example.backend_med.Controller;

import org.example.backend_med.Dto.AvailableHoraireDTO;
import org.example.backend_med.Dto.HoraireDTO;
import org.example.backend_med.Models.Horaire;
import org.example.backend_med.Models.Medecin;
import org.example.backend_med.Repository.MedecinRepo;
import org.example.backend_med.Services.IHoraire;
import org.example.backend_med.Services.IMedecin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/horaires")
@CrossOrigin(origins = "*")
public class HoraireController {

    @Autowired
    private MedecinRepo medecinRepo;

    @Autowired
    private IHoraire horaireService;

    @Autowired
    private IMedecin medecinService;

    @PostMapping
    public ResponseEntity<?> createHoraire(@RequestBody List<HoraireDTO> horairesDto) {
        try {

            List<Horaire> horaires = horairesDto.stream().map(dto -> {

                Horaire h = new Horaire();

                // DATE (LocalDate)
                h.setDate(dto.getDate());

                // TIME (LocalTime)
                if (dto.getHeureDebut() == null || dto.getHeureFin() == null) {
                    throw new IllegalArgumentException("HeureDebut et HeureFin sont obligatoires");
                }

                if (dto.getHeureDebut().isAfter(dto.getHeureFin())) {
                    throw new IllegalArgumentException("HeureDebut doit être avant HeureFin");
                }

                h.setHeureDebut(dto.getHeureDebut());
                h.setHeureFin(dto.getHeureFin());

                h.setStatus(dto.getStatus());

                // MEDICAL RELATION
                Medecin m = medecinRepo.findById(dto.getMedecinId())
                        .orElseThrow(() -> new RuntimeException("Medecin not found"));

                h.setMedecin(m);

                return h;
            }).toList();

            List<Horaire> created = horaireService.createHoraire(horaires);

            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getHoraireById(@PathVariable Long id) {
        Optional<Horaire> horaire = horaireService.getHoraireById(id);
        if (horaire.isPresent()) {
            return ResponseEntity.ok(horaire.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Horaire non trouvé avec l'ID: " + id);
    }

    // Get all
    @GetMapping
    public ResponseEntity<List<Horaire>> getAllHoraires() {
        return ResponseEntity.ok(horaireService.getAllHoraires());
    }

    // Get all by medecin
    @GetMapping("/medecin/{medecinId}")
    public ResponseEntity<List<Horaire>> getAllHorairesByMedecin(@PathVariable Long medecinId) {
        return ResponseEntity.ok(horaireService.getHorairesByMedecinId(medecinId));
    }

    // ✅ Get available slots for a doctor — filtered by date
    @GetMapping("/medecin/{medecinId}/available")
    public ResponseEntity<List<AvailableHoraireDTO>> getAvailableTime(
            @PathVariable Long medecinId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        if (date == null) date = LocalDate.now();

        List<AvailableHoraireDTO> available = horaireService
                .getAvailableTimeForDoctorOnDate(medecinId, date);

        return ResponseEntity.ok(available);
    }

    // Get available slots with partial booking info
    @GetMapping("/medecin/{medecinId}/available-slots")
    public ResponseEntity<List<AvailableHoraireDTO>> getAvailableHorairesWithSlots(
            @PathVariable Long medecinId) {
        return ResponseEntity.ok(horaireService.getAvailableHorairesWithSlots(medecinId));
    }

    // ✅ Get by date (replaces /jour/{joursSemaine})
    @GetMapping("/date/{date}")
    public ResponseEntity<List<Horaire>> getHorairesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(horaireService.getHorairesByDate(date));
    }

    // Get by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Horaire>> getHorairesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(horaireService.getHorairesByStatus(status));
    }

    // ✅ Get by medecin and date (replaces /medecin/{id}/jour/{jour})
    @GetMapping("/medecin/{medecinId}/date/{date}")
    public ResponseEntity<List<Horaire>> getHorairesByMedecinAndDate(
            @PathVariable Long medecinId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(horaireService.getHorairesByMedecinAndDate(medecinId, date));
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<?> updateHoraire(@PathVariable Long id, @RequestBody Horaire horaire) {
        try {
            Horaire updated = horaireService.updateHoraire(id, horaire);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHoraire(@PathVariable Long id) {
        try {
            horaireService.deleteHoraire(id);
            return ResponseEntity.ok("Horaire supprimé avec succès");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}