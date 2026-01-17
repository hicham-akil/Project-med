package org.example.backend_med.Models;


import jakarta.persistence.*;
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
    @ManyToMany
    @JoinTable(
            name = "medecin_specialite",
            joinColumns = @JoinColumn(name = "medecin_id"),
            inverseJoinColumns = @JoinColumn(name = "specialite_id")
    )
    private List<Specialite>  specialites;

}
