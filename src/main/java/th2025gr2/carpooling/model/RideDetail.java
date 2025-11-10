package th2025gr2.carpooling.model;

import jakarta.persistence.*;

@Entity
@Table(name = "RideDetails")
public class RideDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ride_id", nullable = false, unique = true)
    private Ride ride;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "special_ride_id", nullable = false)
    private SpecialRide specialRide;
}
