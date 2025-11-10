package th2025gr2.carpooling.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "PlannedRides")
public class PlannedRide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "start_latitude", nullable = false)
    private Double startLatitude;
    @Column(name = "start_longitude", nullable = false)
    private Double startLongitude;
    @Column(name = "end_latitude", nullable = false)
    private Double endLatitude;
    @Column(name = "end_longitude", nullable = false)
    private Double endLongitude;
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
}
