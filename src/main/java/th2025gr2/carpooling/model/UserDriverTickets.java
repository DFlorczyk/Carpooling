package th2025gr2.carpooling.model;

import jakarta.persistence.*;

@Entity
@Table(name = "UserDriverTickets")
public class UserDriverTickets {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    @Lob
    @Column(name = "driver_license", nullable = false)
    private byte[] driverLicense;
}