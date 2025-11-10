package th2025gr2.carpooling.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "Cities")
@Getter
@Setter
@NoArgsConstructor
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String name;
    @OneToMany(mappedBy = "city", fetch = FetchType.LAZY)
    private List<User> users;
}
