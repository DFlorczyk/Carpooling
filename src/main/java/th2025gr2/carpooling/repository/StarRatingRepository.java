package th2025gr2.carpooling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import th2025gr2.carpooling.model.StarRating;

import java.util.Optional;

@Repository
public interface StarRatingRepository extends JpaRepository<StarRating, Long> {
    Optional<StarRating> findByValue(Long value);
}