package th2025gr2.carpooling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import th2025gr2.carpooling.model.PlannedRide;
import th2025gr2.carpooling.model.UserProfile;
import java.util.List;

@Repository
public interface PlannedRideRepository extends JpaRepository<PlannedRide, Long> {
    List<PlannedRide> findByUser(UserProfile user);
}