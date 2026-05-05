package th2025gr2.carpooling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import th2025gr2.carpooling.model.TicketMessage;
import th2025gr2.carpooling.model.UserDriverTickets;

import java.util.List;

public interface TicketMessageRepository extends JpaRepository<TicketMessage, Long> {
    List<TicketMessage> findByTicketOrderBySentAtAsc(UserDriverTickets ticket);
    long countByTicketAndFromAdminFalseAndReadByAdminFalse(UserDriverTickets ticket);
}