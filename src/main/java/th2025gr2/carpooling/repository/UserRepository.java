package th2025gr2.carpooling.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import th2025gr2.carpooling.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}