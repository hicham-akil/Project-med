package org.example.backend_med.Controller;

import org.example.backend_med.Models.Notification;
import org.example.backend_med.Services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Get all notifications of a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getAllNotifications(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                notificationService.getAllNotificationsOfUser(userId)
        );
    }

    /**
     * Get unread notifications of a user
     */
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                notificationService.getUnreadNotifications(userId)
        );
    }

    /**
     * Count unread notifications (badge)
     */
    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<Long> countUnreadNotifications(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                notificationService.countUnreadNotifications(userId)
        );
    }

    /**
     * Mark one notification as read
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId) {

        notificationService.markAsRead(notificationId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Mark all notifications as read for a user
     */
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @PathVariable Long userId) {

        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }
}
