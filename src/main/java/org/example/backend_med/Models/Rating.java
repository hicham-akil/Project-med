package org.example.backend_med.Models;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Rating {
   @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private RatingVal rating;
    @ManyToOne
    private Medecin medecin;

}
