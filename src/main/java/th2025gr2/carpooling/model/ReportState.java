package th2025gr2.carpooling.model;


import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "ReportStates")
public class ReportState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @OneToMany(mappedBy = "reportState", fetch = FetchType.LAZY)
    private List<Report> reports;
}
