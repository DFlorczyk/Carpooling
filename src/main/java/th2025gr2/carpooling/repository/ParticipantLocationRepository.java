package th2025gr2.carpooling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import th2025gr2.carpooling.dto.ParticipantLocationDTO;
import th2025gr2.carpooling.model.ParticipantLocation;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantLocationRepository extends JpaRepository<ParticipantLocation, Long> {

    Optional<ParticipantLocation> findByRide_IdAndProfile_Id(Long rideId, Long profileId);

    @Query("""
            SELECT new th2025gr2.carpooling.dto.ParticipantLocationDTO(
                pl.profile.id,
                CONCAT(pl.profile.name, ' ', pl.profile.surname),
                rp.role.name,
                pl.latitude,
                pl.longitude
            )
            FROM ParticipantLocation pl
            JOIN RideParticipant rp ON rp.ride.id = pl.ride.id AND rp.user.id = pl.profile.id
            WHERE pl.ride.id = :rideId
            """)
    List<ParticipantLocationDTO> findLocationDTOsByRideId(@Param("rideId") Long rideId);
}
