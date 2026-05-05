package th2025gr2.carpooling.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_profiles")
@Getter @Setter @NoArgsConstructor
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credential_id", nullable = false, unique = true)
    private UserCredential credential;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "is_woman", nullable = false)
    private boolean woman;

    @Column(name = "is_driver", nullable = false)
    private boolean driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private CarDetail carDetail;

//    @OneToOne(mappedBy = "profile", fetch = FetchType.LAZY,
//            cascade = CascadeType.ALL, orphanRemoval = true)
//    private DriverTicket driverTicket;
}