package th2025gr2.carpooling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import th2025gr2.carpooling.model.RideRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface RideRequestRepository extends JpaRepository<RideRequest, Long> {
    List<RideRequest> findByRide_IdAndIsAcceptedIsNull(Long rideId);
    Optional<RideRequest> findByUser_IdAndRide_Id(Long userId, Long rideId);
    List<RideRequest> findByUser_IdOrderByIdDesc(Long userId);

    @Query("""
        SELECT r FROM RideRequest r
        WHERE r.isAccepted IS NULL
          AND r.ride.id IN (
            SELECT rp.ride.id FROM RideParticipant rp
            WHERE rp.user.id = :driverId AND LOWER(rp.role.name) = 'driver'
          )
        """)
    List<RideRequest> findAllPendingForDriver(@Param("driverId") Long driverId);
}
