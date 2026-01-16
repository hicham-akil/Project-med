package org.example.backend_med.Models;


import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Medecin extends  Utlisateur{
    private String specialite;
    private String telephone;
    private String adresse;
    @OneToMany(mappedBy = "medecin")
    private List<Horaire> horairesDisponibles;


}
