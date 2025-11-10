package th2025gr2.carpooling.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "StarRatings")
public class StarRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long value;

    @OneToMany(mappedBy = "starRating", fetch = FetchType.LAZY)
    private List<UserReview> userReviews;
}
