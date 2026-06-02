package th2025gr2.carpooling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import th2025gr2.carpooling.model.RideWaypoint;

import java.util.List;

@Repository
public interface RideWaypointRepository extends JpaRepository<RideWaypoint, Long> {
    List<RideWaypoint> findByRideIdOrderBySequenceOrderAsc(Long rideId);

    @Query("SELECT COALESCE(MAX(w.sequenceOrder), 0) FROM RideWaypoint w WHERE w.ride.id = :rideId")
    int findMaxSequenceOrderByRideId(@Param("rideId") Long rideId);
}
