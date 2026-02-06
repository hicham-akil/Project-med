package org.example.backend_med.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Data
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Utlisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    private String nom;
    private String prenom;
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    private Date dateCreation;
    private Date dateModification;
    private String role;
    @Column(name = "profile_image_url",length = 500)
    private String profileImageUrl;


    @OneToMany(mappedBy = "utilisateur")
    private List<Notification> notifications;
}