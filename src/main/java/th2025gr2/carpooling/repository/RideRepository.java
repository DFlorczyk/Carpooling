package th2025gr2.carpooling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
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
}


