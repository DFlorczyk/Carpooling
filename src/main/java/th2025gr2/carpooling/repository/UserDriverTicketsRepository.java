package th2025gr2.carpooling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import th2025gr2.carpooling.model.User;
import th2025gr2.carpooling.model.UserDriverTickets;

import java.util.List;
import java.util.Optional;

public interface UserDriverTicketsRepository extends JpaRepository<UserDriverTickets, Long> {
    Optional<UserDriverTickets> findByUser(User user);
    List<UserDriverTickets> findByUserDriverFalse();
    void deleteByUser(User user);
}