package th2025gr2.carpooling.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_body", nullable = false, columnDefinition = "TEXT")
    private String reportBody;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_state_id", nullable = false)
    private ReportState reportState;

    @OneToMany(mappedBy = "report", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ReportParticipant> participants;
}
