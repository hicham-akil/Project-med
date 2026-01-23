package org.example.backend_med.Models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.annotation.sql.DataSourceDefinitions;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class RendezVous {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Column(name = "date_heure")
  private LocalDateTime  dateHeure;
  private  String status;
  private String typeConsultation;
  @JsonBackReference
  @ManyToOne
  @JoinColumn(name = "patient_id")
  private Patient patient;
  @ManyToOne
  @JoinColumn(name = "medecin_id")
  private Medecin medecin;

}
