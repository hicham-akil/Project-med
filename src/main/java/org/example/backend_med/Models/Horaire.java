package org.example.backend_med.Models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Generated;

@Entity
@Data
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "idHoraire"
)
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
