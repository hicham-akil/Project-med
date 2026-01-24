package org.example.backend_med.Services;

import org.example.backend_med.Models.Rating;
import org.example.backend_med.Repository.RatingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RatingService {
    @Autowired
    private  RatingRepo ratingRepo;

    public RatingService(RatingRepo ratingRepo) {
        this.ratingRepo = ratingRepo;
    }

    public Rating saveRating(Rating rating) {
        return ratingRepo.save(rating);
    }

    public List<Rating> getAllRatings() {
        return ratingRepo.findAll();
    }

    public Optional<Rating> getRatingById(Long id) {
        return ratingRepo.findById(id);
    }

    public Rating updateRating(Long id, Rating ratingDetails) {
        Rating rating = ratingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Rating not found with id: " + id));

        rating.setRating(ratingDetails.getRating());

        return ratingRepo.save(rating);
    }

    public void deleteRating(Long id) {
        if (!ratingRepo.existsById(id)) {
            throw new RuntimeException("Rating not found with id: " + id);
        }
        ratingRepo.deleteById(id);
    }

    public void deleteAllRatings() {
        ratingRepo.deleteAll();
    }

    public long countRatings() {
        return ratingRepo.count();
    }

    public boolean ratingExists(Long id) {
        return ratingRepo.existsById(id);
    }
}