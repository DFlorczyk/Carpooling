package th2025gr2.carpooling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import th2025gr2.carpooling.dto.RideDTO;
import th2025gr2.carpooling.model.Ride;

import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    @Query("""
        SELECT DISTINCT r
        FROM Ride r
        JOIN r.participants rp
        WHERE rp.user.id = :userId
          AND r.state.id = 1
        """)
    List<Ride> findFinishedRidesByUserId(@Param("userId") Long userId);

    @Query("""
        SELECT new th2025gr2.carpooling.dto.RideDTO(
            r.id,
            r.startLatitude, r.startLongitude,
            r.endLatitude, r.endLongitude,
            r.cost, r.date,
            r.state.name,
            CONCAT(rp.user.name, ' ', rp.user.surname),
            cd.model, cd.color, cd.seatCount, cd.mileage
        )
        FROM Ride r
        LEFT JOIN r.participants rp
            ON rp.role.name = 'driver'
        LEFT JOIN CarDetail cd
            ON cd.user = rp.user
        WHERE r.state.name = :stateName
        """)
    List<RideDTO> findDTOsByStateName(@Param("stateName") String stateName);
}