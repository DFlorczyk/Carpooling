package th2025gr2.carpooling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import th2025gr2.carpooling.model.CarDetail;
import th2025gr2.carpooling.model.User;

import java.util.Optional;

public interface CarDetailRepository extends JpaRepository<CarDetail, Long> {

    Optional<CarDetail> findByUser(User user);
}
