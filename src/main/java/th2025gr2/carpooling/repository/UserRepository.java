package th2025gr2.carpooling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import th2025gr2.carpooling.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
