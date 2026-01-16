package org.example.backend_med.Models;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private String type;
    private Date dateEnvoi;
    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utlisateur utilisateur;
}