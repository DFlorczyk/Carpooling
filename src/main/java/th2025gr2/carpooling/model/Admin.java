package th2025gr2.carpooling.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(name = "hashed_password", nullable = false)
    private String hashedPassword;

    @Column(name = "is_super_admin", nullable = false)
    private boolean superAdmin;
}