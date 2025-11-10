package th2025gr2.carpooling.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "SpecialRides")
public class SpecialRide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @OneToMany(mappedBy = "specialRide", fetch = FetchType.LAZY)
    private List<RideDetail> rideDetails;
}