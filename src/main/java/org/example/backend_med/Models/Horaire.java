package org.example.backend_med.Models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Generated;

@Entity
@Data
public class Horaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHoraire;
    private String joursSemaine;
    private String heureDebut;
    private String heureFin;
    private String status;
    @ManyToOne
    @JoinColumn(name = "medecin_id")
    private Medecin medecin;
}
