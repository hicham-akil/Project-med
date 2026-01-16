package org.example.backend_med.Models;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class Administrateur extends  Utlisateur{
    private String role;
}
