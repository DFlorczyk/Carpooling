package th2025gr2.carpooling.model;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "RideStates")
public class RideState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    @OneToMany(mappedBy = "state", fetch = FetchType.LAZY)
    private List<Ride> rides;
}
