package org.example.backend_med.Controller;

import org.example.backend_med.Dto.AvailableHoraireDTO;
import org.example.backend_med.Dto.HoraireDTO;
import org.example.backend_med.Models.Horaire;
import org.example.backend_med.Models.Medecin;
import org.example.backend_med.Repository.MedecinRepo;
import org.example.backend_med.Services.IHoraire;
import org.example.backend_med.Services.IMedecin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // Create
    @PostMapping
    public ResponseEntity<?> createHoraire(@RequestBody List<HoraireDTO> horairesDto) {
        try {
            List<Horaire> horaires = horairesDto.stream().map(dto -> {
                Horaire h = new Horaire();
                h.setJoursSemaine(dto.getJoursSemaine());
                h.setHeureDebut(dto.getHeureDebut());
                h.setHeureFin(dto.getHeureFin());
                h.setStatus(dto.getStatus());
                h.setMonth(dto.getMonth());
                h.setYear(dto.getYear());

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

    @GetMapping("/medecin/{medecinId}/available")
    public ResponseEntity<List<Horaire>> getAvailableHorairesByMedecin(@PathVariable Long medecinId) {
        return ResponseEntity.ok(horaireService.getAvailableHorairesByMedecinId(medecinId));
    }
    @GetMapping("/medecin/{medecinId}/available-slots")
    public ResponseEntity<List<AvailableHoraireDTO>> getAvailableHorairesWithSlots(@PathVariable Long medecinId) {
        return ResponseEntity.ok(horaireService.getAvailableHorairesWithSlots(medecinId));
    }


    // Get by jour
    @GetMapping("/jour/{joursSemaine}")
    public ResponseEntity<List<Horaire>> getHorairesByJour(@PathVariable String joursSemaine) {
        return ResponseEntity.ok(horaireService.getHorairesByJour(joursSemaine));
    }

    // Get by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Horaire>> getHorairesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(horaireService.getHorairesByStatus(status));
    }

    // Get by medecin and jour
    @GetMapping("/medecin/{medecinId}/jour/{joursSemaine}")
    public ResponseEntity<List<Horaire>> getHorairesByMedecinAndJour(
            @PathVariable Long medecinId,
            @PathVariable String joursSemaine) {
        return ResponseEntity.ok(horaireService.getHorairesByMedecinAndJour(medecinId, joursSemaine));
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