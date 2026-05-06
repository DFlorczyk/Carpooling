package th2025gr2.carpooling.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import th2025gr2.carpooling.enums.Role;

@Entity
@Table(name = "user_credentials")
@Getter @Setter @NoArgsConstructor
public class UserCredential {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "hashed_password", nullable = false)
    private String hashedPassword;

    @Column(name = "is_blocked", nullable = false)
    private boolean blocked;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToOne(mappedBy = "credential", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private UserProfile profile;
}