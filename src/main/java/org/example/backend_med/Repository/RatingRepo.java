package org.example.backend_med.Repository;

import org.example.backend_med.Models.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepo extends JpaRepository<Rating,Long> {
}
