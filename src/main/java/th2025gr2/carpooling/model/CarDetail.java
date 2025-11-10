package th2025gr2.carpooling.model;

import jakarta.persistence.*;

@Entity
@Table(name = "CarDetails")
public class CarDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    @Column(name = "model", nullable = false)
    private String model;
    @Column(name = "seat_count", nullable = false)
    private Short seatCount;
    @Column(name = "mileage", nullable = false)
    private Short mileage;
    @Column(name = "color", nullable = false)
    private String color;
}
