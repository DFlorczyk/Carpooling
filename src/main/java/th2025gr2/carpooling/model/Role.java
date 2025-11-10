package th2025gr2.carpooling.model;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<RideParticipant> participants;
}

