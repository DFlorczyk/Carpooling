package th2025gr2.carpooling.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ride_id", nullable = false, unique = true)
    private Ride ride;
    @Column(name = "cost", nullable = false)
    private Long cost;
    @Column(name = "is_payed", nullable = false)
    private Boolean isPayed;
}
