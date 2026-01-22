package org.example.backend_med.Repository;

import org.example.backend_med.Models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepo extends JpaRepository<Notification, Long> {

    // Fetch all notifications of a user ordered by date
    List<Notification> findByUtilisateurIdOrderByDateEnvoiDesc(Long userId);

    // Fetch all unread notifications of a user ordered by date
    List<Notification> findByUtilisateurIdAndIsReadFalseOrderByDateEnvoiDesc(Long userId);

    // Fetch all unread notifications of a user
    List<Notification> findByUtilisateurIdAndIsReadFalse(Long userId);

    // Count all unread notifications of a user
    long countByUtilisateurIdAndIsReadFalse(Long userId);
}
