package org.example.backend_med.Services;

import org.example.backend_med.Models.Notification;
import org.example.backend_med.Models.NotificationType;
import org.example.backend_med.Models.Utlisateur;
import org.hibernate.annotations.NotFound;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface INotification {
     void notify(Utlisateur user, String message, NotificationType type);
     List<Notification> getAllNotificationsOfUser(Long id);
     List<Notification> getUnreadNotifications(Long userId);

    void markAsRead(Long notificationId);

    void markAllAsRead(Long userId);

    long countUnreadNotifications(Long userId);
}