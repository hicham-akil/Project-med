package org.example.backend_med.Models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Generated;

import java.time.LocalDate;
import java.time.LocalTime;

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

    private LocalDate date;

    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String status;

    @ManyToOne
    @JoinColumn(name = "medecin_id")
    @JsonIgnoreProperties({"horairesDisponibles", "rendezVous", "specialites"})
    private Medecin medecin;
}