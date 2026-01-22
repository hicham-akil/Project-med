package org.example.backend_med.Services;

import org.example.backend_med.Models.Notification;
import org.example.backend_med.Models.NotificationType;
import org.example.backend_med.Models.Utlisateur;
import org.example.backend_med.Repository.NotificationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService implements  INotification {
    @Autowired
   private NotificationRepo notificationRepo;
    @Autowired
    private EmailService emailService;
    @Override
    public void notify(Utlisateur user, String message, NotificationType type) {
        Notification n = new Notification();
        n.setRead(false);
        n.setUtilisateur(user);
        n.setMessage(message);
        n.setType(type);

        notificationRepo.save(n);

        String subject = (type == NotificationType.PATIENT) ? "Confirmation Rendez-vous" : "Nouveau Rendez-vous";

        emailService.send(user.getEmail(), subject, message);
    }
    @Override
    public List<Notification> getAllNotificationsOfUser(Long userId) {
        return notificationRepo.findByUtilisateurIdOrderByDateEnvoiDesc(userId);
    }
    @Override
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepo.findByUtilisateurIdAndIsReadFalseOrderByDateEnvoiDesc(userId);
    }
    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);
        notificationRepo.save(notification);
    }
    @Override
    public void markAllAsRead(Long userId) {
        List<Notification> notifications =
                notificationRepo.findByUtilisateurIdAndIsReadFalse(userId);

        notifications.forEach(n -> n.setRead(true));
        notificationRepo.saveAll(notifications);
    }
    @Override
    public long countUnreadNotifications(Long userId) {
        return notificationRepo.countByUtilisateurIdAndIsReadFalse(userId);
    }
}
