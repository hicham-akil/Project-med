package org.example.backend_med.Services;

import jakarta.transaction.Transactional;
import org.example.backend_med.Models.Notification;
import org.example.backend_med.Models.NotificationType;
import org.example.backend_med.Models.Utlisateur;
import org.example.backend_med.Repository.NotificationRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService implements INotification {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private EmailService emailService;

    @Override
    @Transactional
    public void notify(Utlisateur user, String message, NotificationType type) {

        logger.info("=== START NOTIFICATION ===");
        logger.info("User ID: {}", user.getId());
        logger.info("User Email: {}", user.getEmail());
        logger.info("Message: {}", message);
        logger.info("Type: {}", type);

        // Save notification in DB
        Notification n = new Notification();
        n.setRead(false);
        n.setUtilisateur(user);
        n.setMessage(message);
        n.setType(type);
        n.setDateEnvoi(new java.util.Date());
        n.setCreatedAt(java.time.LocalDateTime.now());

        notificationRepo.save(n);
        logger.info("Notification saved in DB with ID: {}", n.getId());

        try {

            String subject = (type == NotificationType.PATIENT)
                    ? "Confirmation Rendez-vous"
                    : "Nouveau Rendez-vous";

            logger.info("Sending email...");
            logger.info("To: {}", user.getEmail());
            logger.info("Subject: {}", subject);

            emailService.send(user.getEmail(), subject, message);

            logger.info("✅ EMAIL SENT SUCCESSFULLY");
        } catch (Exception e) {
            logger.error("❌ EMAIL FAILED");
            logger.error("To: {}", user.getEmail());
            logger.error("Error Message: {}", e.getMessage());
            logger.error("Stack Trace: ", e);

            // Important: don't rethrow to avoid rollback
        }

        logger.info("=== END NOTIFICATION ===");
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