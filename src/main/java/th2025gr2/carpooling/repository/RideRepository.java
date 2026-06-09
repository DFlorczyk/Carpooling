package th2025gr2.carpooling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import th2025gr2.carpooling.dto.RideDTO;
import th2025gr2.carpooling.model.Ride;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    @Query("""
        SELECT DISTINCT r
        FROM Ride r
        JOIN FETCH r.state
        JOIN r.participants rp
        WHERE rp.user.id = :userId
          AND r.state.name = 'finished'
        """)
    List<Ride> findFinishedRidesByUserId(@Param("userId") Long userId);

    @Query("""
        SELECT new th2025gr2.carpooling.dto.RideDTO(
            r.id,
            r.startLatitude, r.startLongitude,
            r.endLatitude, r.endLongitude,
            r.cost, r.date,
            r.state.name,
            rp.user.id,
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

    @Query(value = """
    SELECT new th2025gr2.carpooling.dto.RideDTO(
        r.id,
        r.startLatitude, r.startLongitude,
        r.endLatitude, r.endLongitude,
        r.cost, r.date,
        r.state.name,
        rp.user.id,
        CONCAT(rp.user.name, ' ', rp.user.surname),
        cd.model, cd.color, cd.seatCount, cd.mileage
    )
    FROM Ride r
    LEFT JOIN r.participants rp
    LEFT JOIN CarDetail cd ON cd.user = rp.user
    WHERE r.state.name = 'not started'
      AND (rp.role.name = 'driver' OR rp IS NULL)
      AND (:dateFrom IS NULL OR r.date >= :dateFrom)
      AND (:dateTo   IS NULL OR r.date <= :dateTo)
      AND (:maxPrice IS NULL OR r.cost <= :maxPrice)
    """)
    List<RideDTO> findFilteredRides(
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo")   LocalDateTime dateTo,
            @Param("maxPrice") Double maxPrice
    );

    @Query("""
    SELECT new th2025gr2.carpooling.dto.RideDTO(
        r.id,
        r.startLatitude, r.startLongitude,
        r.endLatitude, r.endLongitude,
        r.cost, r.date,
        r.state.name,
        rp.user.id,
        CONCAT(rp.user.name, ' ', rp.user.surname),
        cd.model, cd.color, cd.seatCount, cd.mileage
    )
    FROM Ride r
    LEFT JOIN r.participants rp
    LEFT JOIN CarDetail cd ON cd.user = rp.user
    WHERE r.state.name = 'not started'
      AND (rp.role.name = 'driver' OR rp IS NULL)
      AND r.date >= :dateFrom
      AND r.date <= :dateTo
      AND r.cost <= :maxPrice
    """)
    List<RideDTO> findFilteredRidesWithAllParams(
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo")   LocalDateTime dateTo,
            @Param("maxPrice") Double maxPrice
    );

    @Query("""
        SELECT r FROM Ride r
        JOIN FETCH r.state
        LEFT JOIN FETCH r.participants rp
        LEFT JOIN FETCH rp.user
        LEFT JOIN FETCH rp.role
        WHERE UPPER(r.state.name) = 'ACTIVE'
          AND r.date > CURRENT_TIMESTAMP
        ORDER BY r.date ASC
        """)
    List<Ride> findActiveRides();

    @Query("""
        SELECT new th2025gr2.carpooling.dto.RideDTO(
            r.id,
            r.startLatitude, r.startLongitude,
            r.endLatitude, r.endLongitude,
            r.cost, r.date,
            r.state.name,
            rp.user.id,
            CONCAT(rp.user.name, ' ', rp.user.surname),
            cd.model, cd.color, cd.seatCount, cd.mileage
        )
        FROM Ride r
        JOIN r.participants rp
        LEFT JOIN CarDetail cd ON cd.user = rp.user
        WHERE rp.role.name = 'driver'
          AND rp.user.id = :driverId
        ORDER BY r.date DESC
        """)
    List<RideDTO> findDTOsByDriverId(@Param("driverId") Long driverId);

    @Query("""
        SELECT new th2025gr2.carpooling.dto.RideDTO(
            r.id,
            r.startLatitude, r.startLongitude,
            r.endLatitude, r.endLongitude,
            r.cost, r.date,
            r.state.name,
            rp.user.id,
            CONCAT(rp.user.name, ' ', rp.user.surname),
            cd.model, cd.color, cd.seatCount, cd.mileage
        )
        FROM Ride r
        JOIN r.participants rp
        LEFT JOIN CarDetail cd ON cd.user = rp.user
        WHERE rp.role.name = 'driver'
          AND rp.user.id = :driverId
          AND r.state.name = 'active'
        ORDER BY r.date DESC
        """)
    List<RideDTO> findActiveDTOsByDriverId(@Param("driverId") Long driverId);

    @Query("""
        SELECT new th2025gr2.carpooling.dto.RideDTO(
            r.id,
            r.startLatitude, r.startLongitude,
            r.endLatitude, r.endLongitude,
            r.cost, r.date,
            r.state.name,
            rp.user.id,
            CONCAT(rp.user.name, ' ', rp.user.surname),
            cd.model, cd.color, cd.seatCount, cd.mileage
        )
        FROM Ride r
        JOIN r.participants rp
        LEFT JOIN CarDetail cd ON cd.user = rp.user
        WHERE r.id = :rideId
          AND rp.role.name = 'driver'
        """)
    List<RideDTO> findDTOByRideIdAsDriver(@Param("rideId") Long rideId);

    @Query("""
        SELECT new th2025gr2.carpooling.dto.RideDTO(
            r.id,
            r.startLatitude, r.startLongitude,
            r.endLatitude, r.endLongitude,
            r.cost, r.date,
            r.state.name,
            dp.user.id,
            CONCAT(dp.user.name, ' ', dp.user.surname),
            cd.model, cd.color, cd.seatCount, cd.mileage
        )
        FROM Ride r
        JOIN r.participants pp
        JOIN r.participants dp
        LEFT JOIN CarDetail cd ON cd.user = dp.user
        WHERE pp.role.name = 'passenger'
          AND pp.user.id = :passengerProfileId
          AND dp.role.name = 'driver'
          AND r.state.name = 'active'
        ORDER BY r.date DESC
        """)
    List<RideDTO> findActiveDTOsByPassengerId(@Param("passengerProfileId") Long passengerProfileId);
}