package th2025gr2.carpooling.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_messages")
@Getter @Setter @NoArgsConstructor
public class TicketMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_id", nullable = false)
    private UserDriverTickets ticket;

    @Column(name = "from_admin", nullable = false)
    private boolean fromAdmin;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "attachment", columnDefinition = "bytea")
    private byte[] attachment;

    @Column(name = "attachment_type")
    private String attachmentType;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "read_by_admin", nullable = false)
    private boolean readByAdmin;
}