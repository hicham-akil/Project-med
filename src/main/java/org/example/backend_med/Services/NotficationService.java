package org.example.backend_med.Services;

import org.example.backend_med.Models.Notification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class NotficationService implements INotification{
    @Override
    public Notification createNotification(Notification notification) {
        return null;
    }

    @Override
    public List<Notification> createNotifications(List<Notification> notifications) {
        return List.of();
    }

    @Override
    public Optional<Notification> getNotificationById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Notification> getAllNotifications() {
        return List.of();
    }

    @Override
    public List<Notification> getNotificationsByUserId(Long userId) {
        return List.of();
    }

    @Override
    public List<Notification> getNotificationsByMedecinId(Long medecinId) {
        return List.of();
    }

    @Override
    public List<Notification> getUnreadNotificationsByUserId(Long userId) {
        return List.of();
    }

    @Override
    public List<Notification> getNotificationsByType(String type) {
        return List.of();
    }

    @Override
    public List<Notification> getNotificationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return List.of();
    }

    @Override
    public Notification updateNotification(Long id, Notification notification) {
        return null;
    }

    @Override
    public Notification markAsRead(Long id) {
        return null;
    }

    @Override
    public void markAllAsReadByUserId(Long userId) {

    }

    @Override
    public void deleteNotification(Long id) {

    }

    @Override
    public void deleteNotificationsByUserId(Long userId) {

    }

    @Override
    public void deleteNotificationsOlderThan(LocalDateTime date) {

    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public long countNotifications() {
        return 0;
    }

    @Override
    public long countUnreadNotificationsByUserId(Long userId) {
        return 0;
    }
}
