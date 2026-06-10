package th2025gr2.carpooling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import th2025gr2.carpooling.model.Notification;
import th2025gr2.carpooling.model.UserProfile;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(UserProfile user);
}