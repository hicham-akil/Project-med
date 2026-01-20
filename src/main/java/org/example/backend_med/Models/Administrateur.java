package org.example.backend_med.Models;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity

public class Administrateur extends  Utlisateur{
    private String role;
}
