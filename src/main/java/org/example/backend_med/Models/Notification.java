package org.example.backend_med.Models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    // Rename field to avoid conflicts
    @Column(name = "is_read",nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isRead = false;

    private LocalDateTime createdAt = LocalDateTime.now();
    private Date dateEnvoi;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utlisateur utilisateur;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

}
