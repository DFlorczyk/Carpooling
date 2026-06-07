package th2025gr2.carpooling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import th2025gr2.carpooling.model.RideParticipant;

@Repository
public interface RideParticipantRepository extends JpaRepository<RideParticipant, Long> {
}
