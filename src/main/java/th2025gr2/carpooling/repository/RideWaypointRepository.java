package th2025gr2.carpooling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import th2025gr2.carpooling.model.RideWaypoint;

import java.util.List;

@Repository
public interface RideWaypointRepository extends JpaRepository<RideWaypoint, Long> {
    List<RideWaypoint> findByRideIdOrderBySequenceOrderAsc(Long rideId);
}
