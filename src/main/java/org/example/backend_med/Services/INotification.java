package org.example.backend_med.Services;

import org.example.backend_med.Models.Notification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface INotification {

    // Create
    Notification createNotification(Notification notification);

    // Create - Bulk
    List<Notification> createNotifications(List<Notification> notifications);

    // Read - Single
    Optional<Notification> getNotificationById(Long id);

    // Read - All
    List<Notification> getAllNotifications();

    // Read - By User/Patient
    List<Notification> getNotificationsByUserId(Long userId);

    // Read - By Medecin
    List<Notification> getNotificationsByMedecinId(Long medecinId);

    // Read - Unread Notifications
    List<Notification> getUnreadNotificationsByUserId(Long userId);

    // Read - By Type
    List<Notification> getNotificationsByType(String type);

    // Read - By Date Range
    List<Notification> getNotificationsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    // Update
    Notification updateNotification(Long id, Notification notification);

    // Update - Mark as Read
    Notification markAsRead(Long id);

    // Update - Mark All as Read
    void markAllAsReadByUserId(Long userId);

    // Delete
    void deleteNotification(Long id);

    // Delete - By User
    void deleteNotificationsByUserId(Long userId);

    // Delete - Old Notifications
    void deleteNotificationsOlderThan(LocalDateTime date);

    // Check existence
    boolean existsById(Long id);

    // Count
    long countNotifications();

    // Count - Unread
    long countUnreadNotificationsByUserId(Long userId);
}