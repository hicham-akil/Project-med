package org.example.backend_med.Repository;

import org.example.backend_med.Models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepo extends JpaRepository<Notification,Long> {
}
