package th2025gr2.carpooling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import th2025gr2.carpooling.model.RideParticipant;

import java.util.List;
import java.util.Optional;

@Repository
public interface RideParticipantRepository extends JpaRepository<RideParticipant, Long> {
    List<RideParticipant> findByRide_Id(Long rideId);
    Optional<RideParticipant> findByUser_IdAndRide_Id(Long userId, Long rideId);
}
