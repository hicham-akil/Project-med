package org.example.backend_med.Controller;

import org.example.backend_med.Models.Rating;
import org.example.backend_med.Services.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ratings")
@CrossOrigin(origins = "*")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    // Create rating
    @PostMapping
    public ResponseEntity<Rating> createRating(@RequestBody Rating rating) {
        Rating savedRating = ratingService.saveRating(rating);
        return new ResponseEntity<>(savedRating, HttpStatus.CREATED);
    }

    // Get all ratings
    @GetMapping
    public ResponseEntity<List<Rating>> getAllRatings() {
        return ResponseEntity.ok(ratingService.getAllRatings());
    }

    // Get rating by id
    @GetMapping("/{id}")
    public ResponseEntity<Rating> getRatingById(@PathVariable Long id) {
        Optional<Rating> rating = ratingService.getRatingById(id);
        return rating
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update rating
    @PutMapping("/{id}")
    public ResponseEntity<Rating> updateRating(
            @PathVariable Long id,
            @RequestBody Rating ratingDetails
    ) {
        Rating updatedRating = ratingService.updateRating(id, ratingDetails);
        return ResponseEntity.ok(updatedRating);
    }

    // Delete rating by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long id) {
        ratingService.deleteRating(id);
        return ResponseEntity.noContent().build();
    }

    // Delete all ratings
    @DeleteMapping
    public ResponseEntity<Void> deleteAllRatings() {
        ratingService.deleteAllRatings();
        return ResponseEntity.noContent().build();
    }

    // Count ratings
    @GetMapping("/count")
    public ResponseEntity<Long> countRatings() {
        return ResponseEntity.ok(ratingService.countRatings());
    }

    // Check if rating exists
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> ratingExists(@PathVariable Long id) {
        return ResponseEntity.ok(ratingService.ratingExists(id));
    }
}
