package th2025gr2.carpooling.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private boolean is_driver;
    @Column(nullable = false, name = "hashed_password")
    private String hashedPassword;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    @Column(unique = true, name = "phone_number")
    private String phoneNumber;
    @Column(unique = true)
    private String email;
    @Column(nullable = false)
    private boolean is_woman;
    @Column(nullable = false)
    private boolean is_blocked;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private UserDriverTickets driverTickets;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PlannedRide> plannedRides;
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private CarDetail carDetails;
    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY)
    private List<UserReview> sentReviews;
    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
    private List<UserReview> receivedReviews;
}
