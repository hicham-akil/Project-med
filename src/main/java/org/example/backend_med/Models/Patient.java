package org.example.backend_med.Models;


import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Data

public class Patient extends Utlisateur {
    private Date dateNaissance;
    private String adresse;
    private String telephone;
    @OneToMany(mappedBy = "patient")
    private List<RendezVous> rendezVous;

}
