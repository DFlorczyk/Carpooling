package th2025gr2.carpooling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import th2025gr2.carpooling.model.Ride;

import java.time.LocalDateTime;
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

    /**
     * Pobiera aktywne przejazdy (state.name = 'ACTIVE'), posortowane od najnowszych.
     */
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

    /**
     * Wyszukiwanie przejazdów wg przybliżonej lokalizacji (bounding box) i daty.
     */
    @Query("""
        SELECT r FROM Ride r
        JOIN FETCH r.state
        WHERE UPPER(r.state.name) = 'ACTIVE'
          AND r.date > :now
          AND r.startLatitude BETWEEN :minLat AND :maxLat
          AND r.startLongitude BETWEEN :minLng AND :maxLng
        ORDER BY r.date ASC
    """)
    List<Ride> findByStartLocationNear(
            @Param("minLat") double minLat,
            @Param("maxLat") double maxLat,
            @Param("minLng") double minLng,
            @Param("maxLng") double maxLng,
            @Param("now") LocalDateTime now
    );
}
