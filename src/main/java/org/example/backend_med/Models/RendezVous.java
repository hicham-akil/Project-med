package org.example.backend_med.Models;


import jakarta.annotation.sql.DataSourceDefinitions;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class RendezVous {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private Date dateHeure;
  private  String Status;
  private String typeConsultation;
  @ManyToOne
  @JoinColumn(name = "patient_id")
  private Patient patient;

}
