package th2025gr2.carpooling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import th2025gr2.carpooling.model.Ride;
import th2025gr2.carpooling.model.UserProfile;
import th2025gr2.carpooling.model.UserReview;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserReviewRepository extends JpaRepository<UserReview, Long> {

    boolean existsBySenderAndReceiverAndRide(UserProfile sender, UserProfile receiver, Ride ride);

    List<UserReview> findByReceiver(UserProfile receiver);

    List<UserReview> findBySenderAndRide(UserProfile sender, Ride ride);

    @Query("SELECT AVG(ur.starRating.value) FROM UserReview ur WHERE ur.receiver = :receiver")
    Optional<Double> findAverageRatingByReceiver(@Param("receiver") UserProfile receiver);
}