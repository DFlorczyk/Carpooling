package th2025gr2.carpooling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import th2025gr2.carpooling.model.RideRole;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RideRole, Long> {

    Optional<RideRole> findByNameIgnoreCase(String name);
}
