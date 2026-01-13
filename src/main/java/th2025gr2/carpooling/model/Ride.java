package th2025gr2.carpooling.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rides")
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_latitude", nullable = false)
    private Double startLatitude;
    @Column(name = "start_longitude", nullable = false)
    private Double startLongitude;
    @Column(name = "end_latitude", nullable = false)
    private Double endLatitude;
    @Column(name = "end_longitude", nullable = false)
    private Double endLongitude;
    @Column(name = "date", nullable = false)
    private LocalDateTime date;
    @Column(name = "cost", nullable = false)
    private Double cost;
    @Column(name = "is_payed", nullable = false)
    private Boolean isPayed;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "state_id", nullable = false)
    private RideState state;
    @OneToMany(mappedBy = "ride", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RideRequest> rideRequests;
    @OneToMany(mappedBy = "ride", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RideParticipant> participants;
}
