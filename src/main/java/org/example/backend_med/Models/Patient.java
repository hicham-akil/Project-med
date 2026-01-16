package org.example.backend_med.Models;


import jakarta.persistence.Entity;
import lombok.Data;

import java.util.Date;

@Entity
@Data

public class Patient extends Utlisateur {
    private Date dateNaissance;
    private String adresse;
    private String telephone;

}
